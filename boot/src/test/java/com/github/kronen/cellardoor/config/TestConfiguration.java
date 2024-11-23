package com.github.kronen.cellardoor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfiguration {

  public static final String MONGO_VERSION = "mongo:8.0.3";

}
