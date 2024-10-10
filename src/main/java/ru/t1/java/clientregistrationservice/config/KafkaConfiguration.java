//package ru.t1.java.clientregistrationservice.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.listener.CommonErrorHandler;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//import org.springframework.util.backoff.FixedBackOff;
//import ru.t1.java.clientregistrationservice.adapter.kafka.KafkaTransactProducer;
//import ru.t1.java.clientregistrationservice.adapter.kafka.MessageDeserializer;
//import ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class KafkaConfiguration<T> {
//
//    @Value("${t1.kafka.consumer.group-id}")
//    private String groupId;
//    @Value("${t1.kafka.bootstrap.server}")
//    private String servers;
//    @Value("${t1.kafka.session.timeout.ms:15000}")
//    private String sessionTimeout;
//    @Value("${t1.kafka.max.partition.fetch.bytes:300000}")
//    private String maxPartitionFetchBytes;
//    @Value("${t1.kafka.max.poll.records:1}")
//    private String maxPollRecords;
//    @Value("${t1.kafka.max.poll.interval.ms:3000}")
//    private String maxPollIntervalsMs;
//    @Value("${t1.kafka.topic.client_transactions}")
//    private String clientTopic;
//
//    @Bean
//    public ConsumerFactory<String, TransactionDto> consumerListenerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.t1.java.clientregistrationservice.app.domain.dto.TransactionDto");
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
//        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalsMs);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
//        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);
//        DefaultKafkaConsumerFactory<String, TransactionDto> factory = new DefaultKafkaConsumerFactory<>(props);
//        factory.setKeyDeserializer(new StringDeserializer());
//        return factory;
//    }
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> kafkaListenerContainerFactory(
//            @Qualifier("consumerListenerFactory") ConsumerFactory<String, TransactionDto> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factoryBuilder(consumerFactory, factory);
//        return factory;
//    }
//
//    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
//        factory.setConsumerFactory(consumerFactory);
//        factory.setBatchListener(true);
//        factory.setConcurrency(1);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        factory.getContainerProperties().setPollTimeout(5000);
//        factory.getContainerProperties().setMicrometerEnabled(true);
//        factory.setCommonErrorHandler(errorHandler());
//    }
//
//    private CommonErrorHandler errorHandler() {
//        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
//        handler.addNotRetryableExceptions(IllegalStateException.class);
//        handler.setRetryListeners((record, ex, deliveryAttempt) ->
//                log.error(" RetryListeners message = {}, offset = {} deliveryAttempt = {}", ex.getMessage(), record.offset(), deliveryAttempt));
//        return handler;
//    }
//
//    @Bean("transact")
//    @Primary
//    public KafkaTemplate<String, T> kafkaTransactTemplate(@Qualifier("producerTransactFactory") ProducerFactory<String, T> producerPatFactory) {
//        return new KafkaTemplate<>(producerPatFactory);
//    }
//
//    @Bean
//    @ConditionalOnProperty(value = "t1.kafka.producer.enable",
//            havingValue = "true",
//            matchIfMissing = true)
//    public KafkaTransactProducer producerTransact(@Qualifier("transact") KafkaTemplate<String, TransactionDto> template) {
//        template.setDefaultTopic(clientTopic);
//        return new KafkaTransactProducer(template);
//    }
//
//    @Bean("producerTransactFactory")
//    public ProducerFactory<String, T> producerTransactFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
//        return new DefaultKafkaProducerFactory<>(props);
//    }
//}
