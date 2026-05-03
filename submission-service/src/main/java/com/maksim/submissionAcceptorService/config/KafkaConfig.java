package com.maksim.submissionAcceptorService.config;

import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
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
    private String solutionSubmittedTopic;

    @Value("${solution.judged.event.topic}")
    private String solutionJudgedTopic;

    @Value("${standings.update.event.topic}")
    private String standingsUpdateTopic;

    @Value("${consumer.group_id}")
    private String groupId;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrap;

    @Bean
    ConsumerFactory<Integer, SolutionJudgedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new IntegerDeserializer(),
                new JacksonJsonDeserializer<>(SolutionJudgedEvent.class)
        );
    }

    @Bean("solutionJudgedKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Integer, SolutionJudgedEvent> solutionJudgedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, SolutionJudgedEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    NewTopic solutionSubmittedTopic() {
        return TopicBuilder.name(solutionSubmittedTopic)
                .partitions(3)
                .build();
    }

    @Bean
    NewTopic solutionJudgedTopic() {
        return TopicBuilder.name(solutionJudgedTopic)
                .partitions(3)
                .build();
    }

    @Bean
    NewTopic standingsUpdateTopic() {
        return TopicBuilder.name(standingsUpdateTopic)
                .partitions(3)
                .build();
    }
}
