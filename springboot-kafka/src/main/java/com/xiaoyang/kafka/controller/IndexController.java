package com.xiaoyang.kafka.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyang
 */
@Slf4j
@RestController
@RequestMapping("kafka")
public class IndexController {

    private final static String TOPIC = "kafka-test";
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("send/{msg}")
    public String send(@PathVariable String msg){
        //kafkaTemplate.send(TOPIC,msg);
        //kafka 事务
        kafkaTemplate.executeInTransaction(t->{
            t.send(TOPIC,msg);
            if ("error".equals(msg)) {
                throw new RuntimeException("Error ");
            }
            return true;
        });
        return "success";
    }
    @GetMapping("sendt/{msg}")
    @Transactional(rollbackFor = Exception.class)
    public String sendTr(@PathVariable String msg){
        kafkaTemplate.send(TOPIC,msg);
        if ("error".equals(msg)) {
            throw new RuntimeException("Error ");
        }
        return "success";
    }
    @KafkaListener(topics = TOPIC)
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        log.info(cr.toString());

    }

}
