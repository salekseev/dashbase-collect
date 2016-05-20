package io.dashbase.collector;

import java.io.File;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import io.dashbase.collector.sinks.CollectorSink;
import io.dashbase.collector.sinks.SinkConfig;

public class DashbaseCollectorServer
{

  @Inject
  CollectorSink sink;
  
  public static void main(String[] args) throws Exception
  {
    File confFile = new File(args[0]);
    Preconditions.checkNotNull(confFile);
    Preconditions.checkArgument(confFile.isFile() && confFile.exists(), confFile.getAbsolutePath() + " cannot be loaded.");
    JsonFactory jsonFactory = new JsonFactory();
    JsonParser parser = jsonFactory.createParser(confFile);
    ObjectMapper mapper = new ObjectMapper();
    SinkConfig sinkCondfig = mapper.readValue(parser, SinkConfig.class);
    
    Injector injector = Guice.createInjector(new DashbaseCollectorModule(sinkCondfig));
    final DashbaseCollectorRunner appMain = new DashbaseCollectorRunner();
    injector.injectMembers(appMain);

    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      public void run()
      {
        appMain.shutdown();
      }
    });
    
    appMain.runServer();
  }
}
