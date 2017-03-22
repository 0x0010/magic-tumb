package com.iamdigger.magictumblr.wcintf;

import java.util.concurrent.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Sam
 * @since 3.0.0
 */
@EnableAutoConfiguration
@ComponentScans(value = {
    @ComponentScan(value = "com.iamdigger.magictumblr.wcintf")
})
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean(name = "MTS")
  public Executor taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setThreadNamePrefix("MTS");
    scheduler.setPoolSize(4);
    return scheduler;
  }

  @Bean(name = "MTE")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("MTE");
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(20);
    executor.setKeepAliveSeconds(10);
    executor.setQueueCapacity(100);
    return executor;
  }
}
