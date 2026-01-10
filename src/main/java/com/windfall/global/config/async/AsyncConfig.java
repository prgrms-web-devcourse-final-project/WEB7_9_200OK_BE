package com.windfall.global.config.async;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

  @Bean(name = "socketTaskExecutor")
  public Executor socketTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(50); // 기본 및 대기열 초과 시 최대 스레드 수
    executor.setQueueCapacity(100); // 대기열
    executor.setThreadNamePrefix("Socket-Async-");
    executor.initialize();
    return  executor;
  }

}
