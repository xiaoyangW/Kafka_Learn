package com.xiaoyang.kafka;

import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xiaoyang
 */
public class KafkaConsumerApp {
    public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
    public final static String TOPIC = "kafka-test";
    public final static String GROUP_ID = "group.kafka-test";

    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<>(16);
        //反序列化器，和Producer的序列化器对应
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //kafka集群连接
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);

        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
        //订阅主题
        consumer.subscribe(Collections.singletonList(TOPIC));
        //通过正则表达式订阅以kafka-开头的主题
        //consumer.subscribe(Pattern.compile("kafka-*"));
        //指定分区消费,
        consumer.assign(Collections.singletonList(new TopicPartition(TOPIC,0)));

        while(true){

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(record -> System.out.println(record.value()));
        }
    }

}
