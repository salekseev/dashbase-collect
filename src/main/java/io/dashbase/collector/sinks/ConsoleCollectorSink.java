package io.dashbase.collector.sinks;

import java.util.Map;

import org.apache.commons.io.Charsets;

public class ConsoleCollectorSink implements CollectorSink
{
  @Override
  public void add(String name, Map<String, String> params, byte[] data) throws Exception
  {
    System.out.println("console: " + name + " ==> " + new String(data, Charsets.UTF_8));
  }
}
