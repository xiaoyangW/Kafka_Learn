package com.xiaoyang.kafka;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.*;
import java.util.regex.Pattern;

/**
 * @author xiaoyang
 */
@Slf4j
public class KafkaConsumerApp {
    public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
    public final static String TOPIC = "kafka-test";
    public final static String GROUP_ID = "group.kafka-test";
    private static volatile long offset;


    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<>(16);
        //反序列化器，和Producer的序列化器对应
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //kafka集群连接
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);

        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        //关闭自动提交,关闭自动提交后需要手动提交
        //config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
        //订阅主题
        consumer.subscribe(Collections.singletonList(TOPIC));
        //使用，均衡监听器
        /*consumer.subscribe(Collections.singletonList(TOPIC),
                //在均衡监听器
                new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        //当使用者必须放弃某些分区时，将在重新平衡操作期间调用此方法。建议在此回调中将偏移量提交给Kafka或自定义偏移量存储，以防止重复数据。
                        Set<TopicPartition> assignment = consumer.assignment();
                        Map<TopicPartition, OffsetAndMetadata> offsets= new HashMap<>();
                        assignment.forEach(topic->offsets.put(topic,new OffsetAndMetadata(offset)));
                        consumer.commitSync(offsets);
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        //分区重新分配。在分区重新分配完成之后以及在重新分配之前，将调用此方法。
                    }
                }
        );*/
        //通过正则表达式订阅以kafka-开头的主题
        //consumer.subscribe(Pattern.compile("kafka-*"));
        //指定分区消费,
        //consumer.assign(Collections.singletonList(new TopicPartition(TOPIC,0)));
        //consumer自动提交的消费形式。
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(record -> {
                System.out.println(record.value());

            });
        }
        //assignOffset(consumer);
    }

    /**
     * 指定消费位移
     *
     * @param consumer KafkaConsumer
     */
    private static void assignOffset(KafkaConsumer<String, String> consumer) {
        consumer.poll(Duration.ofMillis(2000));
        //获取消费者所分配的分区
        Set<TopicPartition> assignment = consumer.assignment();
        //指定从分区尾部消费
        //Map<TopicPartition, Long> topicPartitionLongMap = consumer.endOffsets(assignment);
        assignment.forEach(partition -> {
            consumer.seek(partition, 1010000);
        });
        //while (true){
        ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(1));
        poll.forEach(record -> {
            log.info("指定位移消费：key-{},value-{}", record.key(), record.value());
        });
        //}
    }

    /**
     * 手动提交
     *
     * @param consumer KafkaConsumer
     */
    private static void manualCommit(KafkaConsumer<String, String> consumer) {
        while (true) {
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(1000));
            if (poll.isEmpty()) {
                break;
            }
            Iterable<ConsumerRecord<String, String>> records = poll.records(TOPIC);
            records.forEach(record -> {
                log.info("手动提交消费：key-{},value-{}", record.key(), record.value());
                log.info("offset:{},partition:{}", record.offset(), record.partition());
            });

            //同步提交
            consumer.commitSync();
            /*//异步提交
            consumer.commitAsync((offsets, exception) -> {
                if (exception == null){
                    log.info("commitAsync:{}",offsets);
                }else {
                    log.error("commitAsync Exception:{}",exception.toString());
                }
            });*/
        }
    }

}
