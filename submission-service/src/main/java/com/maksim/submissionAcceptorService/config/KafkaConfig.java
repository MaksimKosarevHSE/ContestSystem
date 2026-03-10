package com.maksim.submissionAcceptorService.config;

import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
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

    @Value("${standings.update.event.topic=standings-update-event-topic}")
    private String STANDINGS_UPDATE_TOPIC;

    @Value("${consumer.group_id}")
    private String GROUP_ID;

    @Value("${spring.kafka.bootstrap-servers}")
    private String KAFKA_BOOTSTRAP;

    @Bean
    ConsumerFactory<Integer, SolutionJudgedEvent> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props, new IntegerDeserializer(), new JacksonJsonDeserializer<>(SolutionJudgedEvent.class));
    }

    @Bean("factory1")
    public ConcurrentKafkaListenerContainerFactory<Integer, SolutionJudgedEvent> concurrentKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, SolutionJudgedEvent>();
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
