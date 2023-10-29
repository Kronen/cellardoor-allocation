package com.github.kronen.cellardoor.integration;

import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import com.github.kronen.cellardoor.system.database.orderline.MongoOrderLineRepository;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineDocument;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineMapper;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.kronen.cellardoor.config.TestConfiguration.MONGO_6_0_11;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Testcontainers
@DataMongoTest
public class AllocationDatabaseTestIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGO_6_0_11);

    @Autowired
    MongoOrderLineRepository orderLineRepository;

    OrderLineMapper orderLineMapper = new OrderLineMapperImpl();

    private void insertOrderLines() {
        Flux<OrderLineDocument> orderLineFlux = Flux.just(
                OrderLineDocument.builder().orderId("order1").sku("RED-CHAIR").quantity(12).build(),
                OrderLineDocument.builder().orderId("order2").sku("RED-TABLE").quantity(13).build(),
                OrderLineDocument.builder().orderId("order3").sku("BLUE-LIPSTICK").quantity(14).build());

        int expectedThreeSavedOrderLines = 3;
        StepVerifier.create(orderLineRepository.saveAll(orderLineFlux)).expectNextCount(expectedThreeSavedOrderLines)
                .verifyComplete();
    }

    @Test
    public void whenDeleteAll_thenNoOrderLinesExpected() {
        orderLineRepository.deleteAll().as(StepVerifier::create).expectNextCount(0).verifyComplete();
    }

    @Test
    public void whenInsert3_then3AreExpected() {
        insertOrderLines();
        orderLineRepository.findAll().as(StepVerifier::create).expectNextCount(3).verifyComplete();
    }

    @Test
    public void testOrderLineRepositoryCanSaveLines() {
        OrderLine orderLines = OrderLine.builder().orderId("order1").sku("RED-CHAIR").quantity(12).build();
        Mono<OrderLineDocument> lineMono = orderLineRepository.save(orderLineMapper.toOrderLineDocument(orderLines));

        StepVerifier.create(lineMono).assertNext(line -> assertThat(line.getId()).isNotNull()).verifyComplete();
    }

}
