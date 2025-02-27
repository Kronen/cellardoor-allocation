package com.github.kronen.cellardoor.infraestructure.config.mongo;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

  @Bean
  public MongoCustomConversions mongoCustomConversions() {
    return new MongoCustomConversions(
        Arrays.asList(new MongoOffsetDateTimeWriter(), new MongoOffsetDateTimeReader()));
  }
}
