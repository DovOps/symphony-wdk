package com.symphony.devsol.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import jakarta.annotation.PostConstruct;

@AutoConfiguration
@Slf4j
@EnableCaching
@ComponentScan(basePackages = "com.symphony.devsol")
@EnableWebMvc
public class AutoConfig {
  @PostConstruct
  public void init() {
    log.info("Starting WDK Studio");
  }
}
