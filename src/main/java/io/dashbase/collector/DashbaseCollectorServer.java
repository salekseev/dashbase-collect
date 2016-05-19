package io.dashbase.collector;

import io.dashbase.collector.sinks.CollectorSink;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class DashbaseCollectorServer
{

  @Inject
  CollectorSink sink;
  
  public static void main(String[] args) throws Exception
  {
    Injector injector = Guice.createInjector(new DashbaseCollectorModule());
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
