package io.dashbase.collector.sinks;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class CollectorSinkProvider implements Provider<CollectorSink>
{
  
  private static Logger logger = LoggerFactory.getLogger(CollectorSinkProvider.class);
  private static Map<String, CollectorSink> REGISTRY = Maps.newHashMap();
  
  static {
    REGISTRY.put("console", new ConsoleCollectorSink());
    REGISTRY.put("kafka", new KafkaCollectorSink());
  }
  
  public static void registerCollectorSink(String name, CollectorSink collectorSink) {
    if (!REGISTRY.containsKey(name)) {
      REGISTRY.put(name, collectorSink);
    } else {
      logger.error("collector sink with name: " + name + " already registered.");       
    }
  }
  
  @Inject SinkConfig sinkConfig;

  @Override
  public CollectorSink get()
  {    
    CollectorSink sink = null;
    if (sinkConfig.type != null) {
      logger.info("sink type: " + sinkConfig.type);
      sink = REGISTRY.get(sinkConfig.type);
    } else {
      if (sinkConfig.clazz != null) {
        try {
          sink = sinkConfig.clazz.newInstance();
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    }
    
    if (sink == null) {
      logger.info("no sink loaded, defaulting to console sink.");
      sink = new ConsoleCollectorSink();
    }
    
    sink.initialize(sinkConfig.params);
    return sink;
  }

}
