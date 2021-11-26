package com.github.kronen.cellardoor.boot.local;

import static com.github.kronen.cellardoor.config.TestConfiguration.MONGO_VERSION;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration(proxyBeanMethods = false)
class LocalDevTestcontainersConfig {

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer(MONGO_VERSION).withReuse(true);
    }
}
