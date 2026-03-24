package com.maksim.submissionAcceptorService.config;

import com.maksim.submissionAcceptorService.kafka.event.SubmissionJudgingProgressEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${solution.submitted.event.topic}")
    private String SOL_SUB_TOP;

    @Value("${standings.update.event.topic}")
    private String STANDINGS_UPDATE_TOPIC;

    @Value("${consumer.group_id}")
    private String GROUP_ID;

    @Value("${spring.kafka.bootstrap-servers}")
    private String KAFKA_BOOTSTRAP;

    @Bean
    ConsumerFactory<Integer, SubmissionJudgingProgressEvent> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new DefaultKafkaConsumerFactory<>(props, new IntegerDeserializer(), new JacksonJsonDeserializer<>(SubmissionJudgingProgressEvent.class));
    }

    @Bean("factory1")
    public ConcurrentKafkaListenerContainerFactory<Integer, SubmissionJudgingProgressEvent> concurrentKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, SubmissionJudgingProgressEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    NewTopic createTopic1(){
        return TopicBuilder.name(SOL_SUB_TOP)
                        .partitions(3)
                        .build();
    }


    @Bean
    NewTopic createTopic2(){
        return TopicBuilder.name(STANDINGS_UPDATE_TOPIC)
                .partitions(3)
                .build();
    }
}
