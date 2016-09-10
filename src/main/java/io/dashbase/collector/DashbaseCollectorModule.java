package io.dashbase.collector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.dashbase.collector.sink.CollectorSink;
import io.dashbase.collector.sink.CollectorSinkProvider;
import io.dashbase.collector.sink.SinkConfig;

class DashbaseCollectorModule extends AbstractModule {
  private final SinkConfig sinkConfig;
  private final DashbaseCollectorCmdLineArgs cmdlineArgs;

  DashbaseCollectorModule(DashbaseCollectorCmdLineArgs cmdlineArgs, SinkConfig sinkConfig) {
    this.sinkConfig = sinkConfig;
    this.cmdlineArgs = cmdlineArgs;
  }
  
  @Override
  protected void configure() {
    bind(SinkConfig.class).toInstance(sinkConfig);
    bind(CollectorSink.class).toProvider(CollectorSinkProvider.class).in(Singleton.class);

    bind(DashbaseCollectorCmdLineArgs.class).toInstance(cmdlineArgs);
  }
}
