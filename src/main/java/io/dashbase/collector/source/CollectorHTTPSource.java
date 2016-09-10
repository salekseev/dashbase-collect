package io.dashbase.collector.source;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.inject.Inject;

import io.dashbase.collector.DashbaseCollectorCmdLineArgs;
import io.dashbase.collector.sink.CollectorSink;
import spark.Request;

import static spark.Spark.before;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;

public class CollectorHTTPSource implements CollectorSource {
    private static final Logger logger = LoggerFactory.getLogger(CollectorHTTPSource.class);

    @Inject
    private CollectorSink sink;

    @Inject
    private DashbaseCollectorCmdLineArgs cmdLineArgs;

    @Override
    public void start() {
        staticFileLocation("/public");
        port(cmdLineArgs.port);
        enableCORS();

        post("/collect/:id", (request, response) -> {
            String name = request.params(":id");
            boolean isBatch = Boolean.valueOf(request.queryParams("isBatch"));

            byte[] data = request.bodyAsBytes();

            if (isValidJSON(data)) {
                sink.add(name, Collections.emptyMap(), data, isBatch);
                return "ok";
            } else {
                logger.error("invalid json: " + request.body());
                response.status(400);
                return "invalid json: " + request.body();
            }
        });

        post("/upload/:id", "multipart/form-data", (request, response) -> {
            //- Servlet 3.x config
            String name = request.params(":id");
            File location = File.createTempFile("dashbase-collect", "dat");  // the directory location where files will be stored
            long maxFileSize = 100000000;  // the maximum size allowed for uploaded files
            long maxRequestSize = maxFileSize;  // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;  // the size threshold after which files will be written to disk
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location.getAbsolutePath(),
                  maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            boolean gzip = gzipEncoded(request);
            Collection<Part> parts = request.raw().getParts();
            for(Part part : parts) {
                try (final InputStream in = gzip ? new GZIPInputStream(part.getInputStream()) : part.getInputStream()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
                    while(true) {
                        String line = br.readLine();
                        if (line == null) break;
                        byte[] buffer = line.getBytes(Charsets.UTF_8);
                        if (isValidJSON(buffer)) {
                            sink.add(name, Collections.emptyMap(), buffer, false);
                        } else {
                            logger.error("skipped invalid json: " + new String(buffer, Charsets.UTF_8));
                        }
                    }
                    br.close();
                    part.delete();
                }
            }
            // cleanup
            multipartConfigElement = null;
            parts = null;
            return "OK";
        });
    }

    @Override
    public void shutdown() {
        stop();
        logger.info("collector shut down");
    }

    private static boolean isValidJSON(final byte[] json) {
        try {
            JsonFactory factory = new JsonFactory();
            final JsonParser parser = factory.createParser(json);
            while (parser.nextToken() != null) {
            }
            return true;
        } catch (Exception jpe) {
            return false;
        }
    }

    private static void enableCORS() {
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    private static boolean gzipEncoded(Request request) {
        boolean gzip = false;

        String encodingHeader = request.headers("Content-Encoding");
        if (encodingHeader != null) {
            gzip = encodingHeader.contains("gzip");
        }
        return gzip;
    }
}
