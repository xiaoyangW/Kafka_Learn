package com.xiaoyang.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.NewPartitions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author xiaoyang
 * Kafka admin
 */
public class KakfkaAdmin {
    public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
    public final static String TOPIC = "kafka-test";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        addTopicPartition(4);
    }

    /**
     * 添加分区
     * @param partitionNum 分区数量
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public static void addTopicPartition(Integer partitionNum) throws ExecutionException, InterruptedException {
        Map<String, Object> config = new HashMap<>(16);
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionNum);
        Map<String, NewPartitions> newPartitionMap = new HashMap<>();
        newPartitionMap.put(TOPIC,newPartitions);
        AdminClient adminClient = AdminClient.create(config);
        CreatePartitionsResult partitions = adminClient.createPartitions(newPartitionMap);
        partitions.all().get();
        adminClient.close();
    }
}
