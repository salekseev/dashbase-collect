package io.dashbase.collector.sinks;

import java.util.Map;

import com.google.common.collect.Maps;

public class SinkConfig {
  public String type = null;
  public Class<CollectorSink> clazz = null;
  public Map<String, Object> params = Maps.newHashMap();
}
