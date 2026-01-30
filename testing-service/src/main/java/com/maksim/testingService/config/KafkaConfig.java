package com.maksim.testingService.config;


import com.maksim.testingService.event.SolutionSubmittedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    String groupId;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    String trustedPackages;

    @Bean
    ConsumerFactory<Integer, SolutionSubmittedEvent> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(props, new IntegerDeserializer(), new JacksonJsonDeserializer<>(SolutionSubmittedEvent.class));
    }

    @Bean("factory1")
    public ConcurrentKafkaListenerContainerFactory<Integer, SolutionSubmittedEvent> concurrentKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, SolutionSubmittedEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
