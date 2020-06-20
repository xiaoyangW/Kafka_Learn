[TOC]

### Kafka学习日志

#### Kafka安装与使用

##### 环境准备

- `centos + java` 运行环境，这里我分别准备三台centos虚拟机`192.168.146.151，192.168.146.152，192.168.146.153`
- 安装`zookeeper`：安装包下载 http://www-eu.apache.org/dist/zookeeper/stable/zookeeper-3.4.12.tar.gz，zookeeper集群`192.168.146.151:2181,192.168.146.152:2181,192.168.146.153:2181`
- kafka安装包下载地址：http://kafka.apache.org/downloads，这里我下载了最新版本`2.5.0`：[kafka_2.13-2.5.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.5.0/kafka_2.13-2.5.0.tgz)

##### 安装配置

- 解压kafka安装包，`tar -zxvf kafka_2.13-2.5.0.tgz` ,解压后目录

  ![kafka_dir](http://s1.wailian.download/2020/05/16/kafka_dir.png)

- 修改config目录下`server.properties`属性文件，需要关注的以下几个参数：

  ```properties
  #表示broker的编号，如果集群中有多个broker，则每个broker的编号需要设置的不同
  broker.id=0
  #brokder对外提供的服务入口地址，
  listeners=PLAINTEXT://192.168.146.151:9092
  #设置存放消息日志文件的地址，如果目录不存在程序会自动创建
  log.dirs=/tmp/kafka-logs
  #Kafka所需Zookeeper集群地址，使用`,`分隔
  zookeeper.connect=192.168.146.151:2181,192.168.146.152:2181,192.168.146.153:2181
  ```

- 执行启动命令

  ```shell
  bin/kafka-server-start.sh config/server.properties
  ```

  启动成功会有如下log日志显示

  ![kafka_start_log](http://s1.wailian.download/2020/05/16/kafka_start_log.png)

##### 简单使用

- 创建topic，命令如下

  ```shell
  #创建topic
  bin/kafka-topics.sh --zookeeper 192.168.146.151:2181,192.168.146.152:2181,192.168.146.153:2181 --create --topic kafka-test --partitions 3 --replication-factor 1
  
  --zookeeper：指定了Kafka所连接的Zookeeper服务地址
  --topic：指定了所要创建主题的名称
  --partitions：指定了分区个数
  --replication-factor：指定了副本因子
  --create：创建主题的动作指令
  
  #查看所有的topic
  bin/kafka-topics.sh --zookeeper localhost:2181 --list
  #查看topic详情
  bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic kafka-test
  ```

- 生产端(producer)发送消息

  ```shell
  bin/kafka-console-producer.sh --broker-list 192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092 --topic kafka-test
  
  --broker-list 指定了连接的Kafka集群的地址
  --topic 指定了发送消息时的主题
  ```

- 消费端(consumer)消费消息

  ```shell
  bin/kafka-console-consumer.sh --bootstrap-server 192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092 --topic kafka-test
  
  --bootstrap-server 指定了连接Kafka集群的地址
  --topic 指定了消费端订阅的主题
  ```

#### Kafka Java客户端介绍

使用Maven依赖，客户端的版本我这里和Kafka版本保持一致使用`2.5.0`版本

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.5.0</version>
</dependency>
```

Spring项目使用Kafka，使用[spring-kafka](https://spring.io/projects/spring-kafka)，[文档](https://docs.spring.io/spring-kafka/docs/2.5.2.RELEASE/reference/html/)

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
    //使用springboot时可以忽略版本号
  <version>2.5.2.RELEASE</version>
</dependency>
```

spring版本，Kafka-Clients版本的兼容关系，见文档地址:https://spring.io/projects/spring-kafka#overview

如图：
![spring-kafka](http://s1.wailian.download/2020/06/18/spring-kafka.png)


#### Kafka Producer 生产者

