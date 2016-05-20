package io.dashbase.collector;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dashbase.collector.sinks.CollectorSink;

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
  
  public void runServer() {    
    int port;
    try {
      port = Integer.parseInt(System.getProperty(PORT_PROPERTY));
    } catch (Exception e) {
      port = DEFAULT_PORT;
    }

    port(port);

    post("/collect/:id", (request, response) -> {      
      String name = request.params(":id");
      sink.add(name, request.params(), request.bodyAsBytes());
      return "ok";
    });
  }
}
