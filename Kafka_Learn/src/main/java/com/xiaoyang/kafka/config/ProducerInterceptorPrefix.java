package com.xiaoyang.kafka.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * @author xiaoyang.wen
 *
 * kafka producer拦截器
 */
@Slf4j
public class ProducerInterceptorPrefix implements ProducerInterceptor<String,String> {

    private volatile long  sendSuccess =0;
    private volatile long  sendFail =0;

    @Override
    public ProducerRecord<String,String> onSend(ProducerRecord<String,String> record) {
        //发送前
        log.info("发送消息：{}",record.topic());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception exception) {
        //发送后
        if (exception == null) {
            log.info("发送成功 RecordMetadata topic:{},partition:{},offset:{}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
            sendSuccess++;
        }else{
            sendFail++;
        }
    }

    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendFail +sendSuccess);
        log.info("Producer 发送成功率：{}",successRatio);
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
