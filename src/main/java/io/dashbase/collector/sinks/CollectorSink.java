package io.dashbase.collector.sinks;

import java.util.Map;

public interface CollectorSink {
  void add(String name, Map<String, String> params, byte[] data) throws Exception;
}
