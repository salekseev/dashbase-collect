package io.dashbase.collector;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.dashbase.collector.sink.SinkConfig;
import io.dashbase.collector.source.CollectorHTTPSource;
import io.dashbase.collector.source.CollectorSource;
import io.dashbase.collector.source.CollectorSyslogSource;

public class DashbaseCollectorServer {
  private static final Logger logger = LoggerFactory.getLogger(DashbaseCollectorServer.class);

  public static void main(String[] args) throws Exception {
    final DashbaseCollectorCmdLineArgs cmdlineArgs;
    try {
      cmdlineArgs = new DashbaseCollectorCmdLineArgs(args);
    } catch(CmdLineException e) {
      logger.error("Exception while parsing cmd line args", e);
      return;
    }

    final CollectorSource collector = createCollectorSource(cmdlineArgs);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        collector.shutdown();
      }
    });

    collector.start();
  }

  private static CollectorSource createCollectorSource(
        DashbaseCollectorCmdLineArgs cmdlineArgs) throws IOException {
    final SinkConfig sinkConfig = createSinkConfig(cmdlineArgs);
    final Injector injector = Guice.createInjector(
          new DashbaseCollectorModule(cmdlineArgs, sinkConfig));
    final CollectorSource collector;
    if (cmdlineArgs.useSyslogServer) {
      collector = new CollectorSyslogSource();
    } else {
      collector = new CollectorHTTPSource();
    }
    injector.injectMembers(collector);
    return collector;
  }

  private static SinkConfig createSinkConfig(
        DashbaseCollectorCmdLineArgs cmdlineArgs) throws IOException {
    JsonFactory jsonFactory = new JsonFactory();
    JsonParser parser = jsonFactory.createParser(cmdlineArgs.sinkConfigFile);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(parser, SinkConfig.class);
  }
}
