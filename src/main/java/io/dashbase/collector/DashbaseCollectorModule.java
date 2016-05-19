package io.dashbase.collector;

import io.dashbase.collector.sinks.CollectorSink;
import io.dashbase.collector.sinks.ConsoleCollectorSink;

import com.google.inject.AbstractModule;

public class DashbaseCollectorModule extends AbstractModule
{
  @Override
  protected void configure()
  {
    bind(CollectorSink.class).to(ConsoleCollectorSink.class).asEagerSingleton();
  }
}
