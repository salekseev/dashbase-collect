package io.dashbase.collector;

import static spark.Spark.post;
import static spark.Spark.stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dashbase.collector.sinks.CollectorSink;

import com.google.inject.Inject;

public class DashbaseCollectorRunner
{
  private static final Logger logger = LoggerFactory.getLogger(DashbaseCollectorRunner.class);
  
  @Inject CollectorSink sink;
  
  public void shutdown() {    
    stop();
    logger.info("collector shut down");
  }
  
  public void runServer() {
    post("/collect/:id", (request, response) -> {      
      String name = request.params(":id");
      sink.add(name, request.params(), request.bodyAsBytes());
      return "ok";
    });
  }
}
