package com.xiaoyang.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumerInterceptorPrefix implements ConsumerInterceptor<String, String> {

    private static final long TIME_OUT = 10 * 1000;

    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        //消费之前
        long now = System.currentTimeMillis();
        Map<TopicPartition, List<ConsumerRecord<String, String>>> partitionListMap = new HashMap<>();
        records.partitions().forEach(partition -> {
            List<ConsumerRecord<String, String>> partitionRecordsList = new ArrayList<>();

            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
            partitionRecords.forEach(record -> {
                if (now - record.timestamp() < TIME_OUT) {
                    partitionRecordsList.add(record);
                }
            });
            if (!partitionRecordsList.isEmpty()) {
                partitionListMap.put(partition, partitionRecordsList);
            }
        });
        return new ConsumerRecords<>(partitionListMap);
    }

    @Override
    public void close() {

    }

    @Override
    public void onCommit(Map offsets) {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
