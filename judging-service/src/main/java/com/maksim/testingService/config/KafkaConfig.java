package com.maksim.testingService.config;


import com.maksim.common.event.SolutionSubmittedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String kafkaBootstrap;

    @Value("${consumer.group_id}")
    String groupId;

    @Value("${test.case.judged.event.topic}")
    String testCaseJudgedEventTopicName;

    @Bean
    ConsumerFactory<String, SolutionSubmittedEvent> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JacksonJsonDeserializer<>(SolutionSubmittedEvent.class));
    }
    @Bean("factory1")
    public ConcurrentKafkaListenerContainerFactory<String, SolutionSubmittedEvent> concurrentKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SolutionSubmittedEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    NewTopic createTopic(){
        return TopicBuilder.name(testCaseJudgedEventTopicName)
                .partitions(3)
                .build();
    }
}
