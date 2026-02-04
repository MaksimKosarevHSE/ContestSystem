package com.maksim.testingService.config;

import com.maksim.testingService.event.JudgingProgress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

//    @Bean
//    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
//        return new LettuceConnectionFactory("localhost", 6379);
//    }

    @Bean("asRedis")
    public ReactiveRedisTemplate<String, JudgingProgress> redisTemplate(
            ReactiveRedisConnectionFactory factory) {

        JacksonJsonRedisSerializer<JudgingProgress> jsonSerializer =
                new JacksonJsonRedisSerializer<>(JudgingProgress.class);

        RedisSerializationContext<String, JudgingProgress> serializationContext =
                RedisSerializationContext.<String, JudgingProgress>newSerializationContext(RedisSerializer.string())
                        .value(jsonSerializer)
                        .hashKey(RedisSerializer.string())
                        .hashValue(jsonSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}

