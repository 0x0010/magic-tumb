package com.iamdigger.magictumblr.wcintf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @author Sam
 * @since 3.0.0
 */
@EnableAutoConfiguration
@ComponentScans(value = {
    @ComponentScan(value = "com.iamdigger.magictumblr.wcintf.controller")
})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}