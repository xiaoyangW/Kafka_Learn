package com.xiaoyang.kafka;

import com.xiaoyang.kafka.config.ProducerInterceptorPrefix;
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
        //拦截器
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName());
        //acks 0,1,-1
        config.put(ProducerConfig.ACKS_CONFIG,"-1");
        KafkaProducer<String,String> producer =  new KafkaProducer<String, String>(config);
        ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>(TOPIC,"Kafka-Producer-Send","Hello,Kafka 1!!");
        ProducerRecord<String,String> producerRecord1 = new ProducerRecord<String, String>(TOPIC,0,System.currentTimeMillis()-500,"Kafka-Producer-Send","Hello,Kafka 2!!");

        //同步发送
        //Future<RecordMetadata> send = producer.send(producerRecord);
        //RecordMetadata recordMetadata = send.get();
        //log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
        //异步发送
        for (int i = 0; i < 1; i++) {
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
                }
            });
        }
        producer.send(producerRecord1, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
            }
        });
        producer.close();
    }

}


