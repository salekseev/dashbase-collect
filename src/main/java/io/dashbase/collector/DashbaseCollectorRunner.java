package io.dashbase.collector;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;
import io.dashbase.collector.sinks.CollectorSink;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.google.inject.Inject;

public class DashbaseCollectorRunner
{
  private static final Logger logger = LoggerFactory.getLogger(DashbaseCollectorRunner.class);
  
  private static final String PORT_PROPERTY = "dashbase.collector.port";
  private static final int DEFAULT_PORT = 4567;
  
  @Inject CollectorSink sink;
  
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
  
  public void runServer() {
    int port;
    try {
      port = Integer.parseInt(System.getProperty(PORT_PROPERTY));
    } catch (Exception e) {
      port = DEFAULT_PORT;
    }
    
    staticFileLocation("/public");

    port(port);

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
      String location = "/tmp/dashbase/";  // the directory location where files will be stored
      long maxFileSize = 100000000;  // the maximum size allowed for uploaded files
      long maxRequestSize = maxFileSize;  // the maximum size allowed for multipart/form-data requests
      int fileSizeThreshold = 1024;  // the size threshold after which files will be written to disk
      MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
      request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
      
      Collection<Part> parts = request.raw().getParts();
      for(Part part : parts) {
        try (final InputStream in = part.getInputStream()) {
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
          part.delete();
        }
      }
      // cleanup
      multipartConfigElement = null;
      parts = null;      
      return "OK";
    });
  }
}
