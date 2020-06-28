package com.xiaoyang.kafka.config;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * @author xiaoyang
 * 自定义反序列化器，反序列化一个Map
 */
public class MapDeserializer implements Deserializer<Map<String,String>> {
    @Override
    public Map<String, String> deserialize(String topic, byte[] data) {
        if (data==null){
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Map<String, String>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
