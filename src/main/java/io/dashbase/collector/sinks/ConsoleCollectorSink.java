package io.dashbase.collector.sinks;

import java.util.Map;

import org.apache.commons.io.Charsets;

public class ConsoleCollectorSink implements CollectorSink
{
  @Override
  public void add(String name, Map<String, String> params, byte[] data, boolean isBatch) throws Exception
  {
    String batchMode = isBatch ? "(batch mode)" : "";
    System.out.println("console " + batchMode + ": " + name + " ==> " + new String(data, Charsets.UTF_8));
  }

  @Override
  public void initialize(Map<String, Object> config)
  {	
  }

  @Override
  public void shutdown()
  {	
  }
}
