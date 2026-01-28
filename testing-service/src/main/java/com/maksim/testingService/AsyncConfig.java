package com.maksim.testingService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

//@Configuration
//public class AsyncConfig {
//    @Value("${workers-count")
//    private int workersCount;
//
//    @Bean(name = "threadPoolExecutor")
//    public ThreadPoolTaskExecutor executor(){
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setMaxPoolSize(workersCount);
//        executor.setCorePoolSize(workersCount);
//        executor.setQueueCapacity(0);
//        executor.setThreadNamePrefix("Worker-");
//        executor.initialize();
//        return executor;
//    }
//}
