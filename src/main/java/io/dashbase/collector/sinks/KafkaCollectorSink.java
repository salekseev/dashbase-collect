package io.dashbase.collector.sinks;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCollectorSink implements CollectorSink {
  private static Logger logger = LoggerFactory.getLogger(KafkaCollectorSink.class);
  private Producer<String, byte[]> producer = null;
  
	@Override
	public void add(String name, Map<String, String> params, byte[] data) throws Exception {
	  ProducerRecord<String, byte[]> msg = new ProducerRecord<String, byte[]>(name, data);
    producer.send(msg);
	}

  @Override
  public void initialize(Map<String, Object> config)
  {
    if (producer != null) {
      logger.error("producer already initialized");
    } else {
      producer = new KafkaProducer<String, byte[]>(config);
      logger.info("producer successfully initialized");
    }
  }

  @Override
  public void shutdown()
  {
    if (producer != null) {
      producer.close();
      producer = null;
      logger.info("producer closed.");
    } else {
      logger.warn("producer already closed.");
    }
  }
}
