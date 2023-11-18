package com.github.kronen.cellardoor.boot.local;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

import static com.github.kronen.cellardoor.config.TestConfiguration.MONGO_6_0_11;

@TestConfiguration(proxyBeanMethods = false)
class LocalDevTestcontainersConfig {

  @Bean
  @ServiceConnection
  public MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(MONGO_6_0_11).withReuse(true);
  }

}
