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

##### 其他配置

- `num.partitiions`:自动创建主题时的分区个数
- `log.retention.ms`:kafka通常根据时间来决定数据可以被保留的时间，除了这个配置还有两个参数`log.retention.minutes、log.retention.hours`，默认使用`log.retention.hours`的配置时间，默认值168小时；如果指定了不止一个参数，Kafka会优先使用具有最小值的那个参数
- `log.retention.bytes`:通过保留消息字节数来判断消息是否过期，通过`log.retention.bytes`来指定，作用在每个分区上；比如一个主题有八个分区并且这个参数设置为1G，那么这个主题最多就可以保留8G的数据
- `log.segment.bytes`:这个设置作用在日志片段上，当消息到达broker时会被追加到分区的当前日志片段上，当日志片段大小到达`log.segment.bytes(默认1G)`指定上限时,当前日志片段就会被关闭，一个新的日志片段就会被打开,关闭的日志片段等待过期
- `log.segment.ms`:这个设置作用是多长时间后日志片段会被关闭，这个设置与`log.segment.bytes`不存在互斥问题，两个都设置时日志片段会在大小或者时间到达上限时被关闭，默认情况下`log.segment.ms`没有设置值
- `message.max.bytes`:通过该设置来限制单个消息的大小，默认值是1000000也就是1MB，消息超过大小broker不会接收

#### Kafka Java客户端介绍

使用Maven依赖，客户端的版本我这里和Kafka版本保持一致使用`2.5.0`版本

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    //这里与kafka版本一致
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

##### 发送一个消息

往`kafka-test`中发送一条消息示例：

```java
public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
public final static String TOPIC = "kafka-test";

public static void main(String[] args) throws ExecutionException, InterruptedException {
	Map<String,Object> config = new HashMap<String, Object>(16);
	//key序列化器
	config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	//value序列化器
	config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	//默认分区器DefaultPartitioner
	//config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class);
	//重试次数
	config.put(ProducerConfig.RETRIES_CONFIG,3);
	//kafka集群连接
	config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BROKER_LIST);
	//拦截器
	//config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName());
	//acks 0,1,-1
	config.put(ProducerConfig.ACKS_CONFIG,"-1");
	KafkaProducer<String,String> producer =  new KafkaProducer<String, String>(config);
	ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>(TOPIC,"Kafka-Producer-Send","Hello,Kafka !!");
	//同步发送
	Future<RecordMetadata> send = producer.send(producerRecord);
    //获取发送元数据
	RecordMetadata recordMetadata = send.get();
	log.info("RecordMetadata topic:{},partition:{},offset:{}",recordMetadata.topic(),recordMetadata.partition(),recordMetadata.offset());
	producer.close();
}
```

##### 发送消息步骤

如上代码所示，发送一个消息的步骤，如下：

1. 初始化`ProducerRecord<>`，ProducerRecord对象是发送消息的实体，主要包含：`TOPIC`、`消息KEY值`、`消息VALUE值`。

2. 配置及初始化`KafkaProducer<>`，配置信息主要有:`BOOTSTRAP_SERVERS_CONFIG`、`KEY_SERIALIZER_CLASS_CONFIG`、`VALUE_SERIALIZER_CLASS_CONFIG`、`RETRIES_CONFIG`,每个配置的意思见上代码；因为消息是通过网络进行传输，所有必须进行序列化，序列化器的作用就是把消息的key/value对象序列化。

3. 发送消息，通过调用`KafkaProducer`对象的`send(ProducerRecord)`方法发送消息。

4. 接着这条记录会被添加到一个记录批次里面，这个批次里所有的消息会被发送到相同的主题和
   分区。会有一个独立的线程来把这些记录批次发送到相应的 Broker 上。

5. Broker成功接收到消息，表示发送成功，返回消息的元数据（包括主题和分区信息以及记录在
   分区里的偏移量）。发送失败，可以选择重试或者直接抛出异常。

   ![send msg](http://s1.wailian.download/2020/06/23/kafka-producer.png)

##### 必要的参数配置

- BOOTSTRAP_SERVERS_CONFIG：kafka连接地址

- KEY_SERIALIZER_CLASS_CONFIG、VALUE_SERIALIZER_CLASS_CONFIG：序列化器，通过序列化器把`key`、`value`序列化用于网络传输，与`consumer`配置的反序列化器对应

- RETRIES_CONFI：重试次数，是在发送失败时，生产者应尝试将消息发送给kafka多少次。默认值为2147483647，其为最大整数

  [其他参数配置](http://kafka.apache.org/documentation/#producerconfigs)

##### 序列化器

消息要到网络上进行传输，必须进行序列化，而序列化器的作用就是如此。
Kafka 提供了默认的字符串序列化器（org.apache.kafka.common.serialization.StringSerializer），
还有整型（IntegerSerializer）和字节数组（BytesSerializer）序列化器，这些序列化器都实现了接口
（org.apache.kafka.common.serialization.Serializer）基本上能够满足大部分场景的需求。

##### 自定义系列化器

实现`org.apache.kafka.common.serialization.Serializer`接口，主要实现`byte[] serialize(String topic, T data);`既可以了，只要将需要序列化的对象转换为一个字节数据，反序列化有对应的发送即可；这里实现一个将`Map<String,String>`对象序列化的序列化器，代码如下：

```java
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
```



#### Kafka Consumer消费者

##### 消费一个消息

```java
public final static String BROKER_LIST = "192.168.146.151:9092,192.168.146.152:9092,192.168.146.153:9092";
public final static String TOPIC = "kafka-test";
public final static String GROUP_ID = "group.kafka-test";
private static volatile Map<TopicPartition, OffsetAndMetadata> offsets=new HashMap<>();

public static void main(String[] args) {
	Map<String, Object> config = new HashMap<>(16);
	//反序列化器，和Producer的序列化器对应
	config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	//kafka集群连接
	config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
	//指定消费者拦截器
	//config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, ConsumerInterceptorPrefix.class.getName());
	//消费组
	config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
	//关闭自动提交,关闭自动提交后需要手动提交
	//config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
	KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
	//订阅主题
	consumer.subscribe(Collections.singletonList(TOPIC));
	//通过正则表达式订阅以kafka-开头的主题
	//consumer.subscribe(Pattern.compile("kafka-*"));
	//指定分区消费,
	//consumer.assign(Collections.singletonList(new TopicPartition(TOPIC,0)));
	//consumer自动提交的消费形式。
	while (true) {
		ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
		records.forEach(record -> {
			log.info("消费：key-{},value-{}", record.key(), record.value());
		});
	}
}
```

##### 消费消息步骤

如上代码所示，消费一个消息的步骤，如下：

1. 配置及初始化`KafkaConsumer<>`，配置信息主要有:`BOOTSTRAP_SERVERS_CONFIG`、`KEY_DESERIALIZER_CLASS_CONFIG`、`VALUE_DESERIALIZER_CLASS_CONFIG`、`GROUP_ID_CONFIG`,每个配置的意思见上代码；key/value对象反序列化器与生产者的序列化器对应。
2. 订阅主题，通过调用`KafkaConsumer`对象的`subscribe(Collection<String> topics)`订阅一个或多个主题。
3. 消费消息，通过调用`KafkaConsumer`对象的`poll(final Duration timeout)`方法获取`ConsumerRecords<>`消息列表对象，遍历列表消费；如上代码



#### SpringBoot中使用Kafka

springboot依赖:

```xml
....
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>2.3.1.RELEASE</version>
	<relativePath/>  
</parent>
....
<dependencies>
    ....
    <dependency>
		<groupId>org.springframework.kafka</groupId>
		<artifactId>spring-kafka</artifactId>
	</dependency>
    ....
</dependencies>
```

