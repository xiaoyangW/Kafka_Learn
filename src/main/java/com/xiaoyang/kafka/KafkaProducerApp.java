package com.xiaoyang.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyang
 *
 */
public class KafkaProducerApp {

    public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
    public final static String TOPIC = "kafka-test";
    public static void main(String[] args) {
        Map<String,Object> config = new HashMap<String, Object>(16);
        //key序列化器
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //value序列化器
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //重试次数
        config.put(ProducerConfig.RETRIES_CONFIG,5);
        //kafka集群连接
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BROKER_LIST);

        KafkaProducer<String,String> producer =  new KafkaProducer<String, String>(config);
        ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>(TOPIC,"Kafka-Producer-Send","Hello,Kafka!!");
        producer.send(producerRecord);

        producer.close();
    }

}


