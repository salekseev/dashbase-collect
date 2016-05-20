package io.dashbase.collector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.dashbase.collector.sinks.CollectorSink;
import io.dashbase.collector.sinks.CollectorSinkProvider;
import io.dashbase.collector.sinks.SinkConfig;

public class DashbaseCollectorModule extends AbstractModule
{
  private final SinkConfig sinkConfig;
  
  public DashbaseCollectorModule(SinkConfig sinkConfig) {
    this.sinkConfig = sinkConfig;
  }
  
  @Override
  protected void configure()
  {
    bind(SinkConfig.class).toInstance(sinkConfig);
    bind(CollectorSink.class).toProvider(CollectorSinkProvider.class).in(Singleton.class);
  }
}
