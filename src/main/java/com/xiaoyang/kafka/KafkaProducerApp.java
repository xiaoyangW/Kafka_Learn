package com.xiaoyang.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author xiaoyang
 *
 */
@Slf4j
public class KafkaProducerApp {

    public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
    public final static String TOPIC = "kafka-test";
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String,Object> config = new HashMap<String, Object>(16);
        //key序列化器
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //value序列化器
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //分区器
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class);
        //重试次数
        config.put(ProducerConfig.RETRIES_CONFIG,5);
        //kafka集群连接
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BROKER_LIST);

        KafkaProducer<String,String> producer =  new KafkaProducer<String, String>(config);
        ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>(TOPIC,"Kafka-Producer-Send","Hello,Kafka!!");
        //同步发送
        //Future<RecordMetadata> send = producer.send(producerRecord);
        //RecordMetadata recordMetadata = send.get();
        //log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
        //异步发送
        producer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
            }
        });
        producer.close();
    }

}


