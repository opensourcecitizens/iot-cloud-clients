package io.client.kafka;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;


/**
 * Sends messages to specified topic in producer.props resource
 * @param <T>
 * @param <T>
 */
public class KafkaProducerClient<T>{
	private  static final Logger log = Logger.getLogger(KafkaProducerClient.class);
	
	private static KafkaProducerClient<?> dsproducer = null;

	public static KafkaProducerClient<?> singleton() throws IOException {
		if (dsproducer == null) {
			dsproducer = new KafkaProducerClient();
		}
		return dsproducer;
	}

	private KafkaProducer<String, T> producer = null;
	private String topic = null;
	private Properties properties = null;

	public KafkaProducerClient() throws IOException {
		InputStream props = Resources.getResource("producer.props").openStream();
		properties = new Properties();
		properties.load(props);
		producer = new KafkaProducer<String, T>(properties);

	}

	public Future<RecordMetadata> send(T message) {

		topic = properties.getProperty("topic.id");
		Future<RecordMetadata> threadFuture = producer.send(new ProducerRecord<String, T>(topic, message));
		
		return threadFuture;

	}

	public Future<RecordMetadata> send(T message, String topic, Future<RecordMetadata> type) {

		Future<RecordMetadata> threadFuture = producer.send(new ProducerRecord<String, T>(topic, message));

		return threadFuture;

	}
	
	public String send(T message, String topic) {

		Future<RecordMetadata> threadFuture = producer.send(new ProducerRecord<String, T>(topic, message));
		String response = (String)"null";
		try {
			response =  (String) ("Response = " +threadFuture.get().toString());;
			log.info("Wrote to topic:"+threadFuture.get().topic()+";  partition:"+threadFuture.get().partition());
		} catch (Exception e) {
			log.error(e,e);
		}
		
		return response;

	}

	public void finalize() throws Throwable {
		producer.close();
		super.finalize();
	}

}
