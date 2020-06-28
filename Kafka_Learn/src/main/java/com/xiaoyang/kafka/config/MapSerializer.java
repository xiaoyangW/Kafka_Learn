package com.xiaoyang.kafka.config;


import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * @author xiaoyang
 * 自定义序列化器，示例序列化一个map
 */
public class MapSerializer implements Serializer<Map<String,String>> {


    @Override
    public byte[] serialize(String topic, Map<String,String> data) {
        if (data==null){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
}
