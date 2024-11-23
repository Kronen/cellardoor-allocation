package com.github.kronen.cellardoor.integration;

import static com.github.kronen.cellardoor.config.TestConfiguration.MONGO_VERSION;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.OrderLineRepository;
import com.github.kronen.cellardoor.infraestructure.batch.mapper.BatchMapperImpl;
import com.github.kronen.cellardoor.infraestructure.batch.repository.BatchRepository;
import com.github.kronen.cellardoor.infraestructure.config.mongo.MongoConfig;
import com.github.kronen.cellardoor.infraestructure.orderline.mapper.OrderLineMapperImpl;
import com.github.kronen.cellardoor.infraestructure.orderline.repository.OrderLineRepositoryPort;
import java.time.OffsetDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@Testcontainers
@DataMongoTest(
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              MongoConfig.class,
              BatchRepository.class,
              OrderLineRepositoryPort.class,
              BatchMapperImpl.class,
              OrderLineMapperImpl.class
            }))
class AllocationRepositoriesTestIT {

  @Container @ServiceConnection
  static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGO_VERSION).withReuse(false);

  @Autowired OrderLineRepository orderLineRepository;

  @Autowired com.github.kronen.cellardoor.domain.allocation.port.BatchPort batchPort;

  OrderLine insertOrderLine(String sku, Integer quantity, String orderId) {
    OrderLine orderLine = OrderLine.builder().orderId(orderId).sku(sku).quantity(quantity).build();

    return orderLineRepository.save(orderLine).block();
  }

  Batch insertBatch(String reference, String sku, Set<OrderLine> orderLines) {
    Batch batch =
        Batch.builder()
            .reference(reference)
            .sku(sku)
            .allocations(orderLines)
            .purchasedQuantity(12)
            .build();
    return this.batchPort.save(batch).block();
  }

  @Test
  void testWhenInsert3Then3AreExpected() {
    StepVerifier.create(
            orderLineRepository
                .deleteAll()
                .thenMany(
                    orderLineRepository.saveAll(
                        Flux.just(
                            OrderLine.builder()
                                .sku("GENERIC-SOFA")
                                .quantity(12)
                                .orderId("order1")
                                .build(),
                            OrderLine.builder()
                                .sku("RED-TABLE")
                                .quantity(13)
                                .orderId("order1")
                                .build(),
                            OrderLine.builder()
                                .sku("GENERIC-SOFA")
                                .quantity(14)
                                .orderId("order2")
                                .build()))))
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void testWhenDeleteAllThenNoOrderLinesExpected() {
    insertOrderLine("GENERIC-SOFA", 12, "order1");

    orderLineRepository.deleteAll().as(StepVerifier::create).verifyComplete();

    orderLineRepository.findAll().as(StepVerifier::create).expectNextCount(0).verifyComplete();
  }

  @Test
  void testOrderLineRepositoryCanSaveALine() {
    OrderLine orderLine =
        OrderLine.builder().orderId("order1").sku("RED-CHAIR").quantity(12).build();

    orderLineRepository
        .save(orderLine)
        .as(StepVerifier::create)
        .assertNext(savedLine -> assertThat(savedLine.getId()).isNotNull())
        .verifyComplete();
  }

  @Test
  void testBatchRepositoryCanSaveABatch() {
    Batch batch =
        Batch.builder()
            .reference("batch1")
            .sku("RUSTY-SOAPDISH")
            .purchasedQuantity(100)
            .eta(OffsetDateTime.now())
            .build();

    this.batchPort
        .save(batch)
        .as(StepVerifier::create)
        .assertNext(
            savedBatch -> {
              assertThat(savedBatch).isEqualTo(batch); // Only compares reference
              assertThat(savedBatch.getSku()).isEqualTo(batch.getSku());
              assertThat(savedBatch.getPurchasedQuantity()).isEqualTo(batch.getPurchasedQuantity());
              assertThat(savedBatch.getEta()).isEqualTo(batch.getEta());
            })
        .verifyComplete();
  }

  @Test
  void testRepositoryCanRetrieveABatchWithAllocations() {
    OrderLine orderLine = insertOrderLine("GENERIC-SOFA", 12, "order1");
    Batch expectedBatch = insertBatch("batch1", "GENERIC-SOFA", Set.of(orderLine));

    batchPort
        .findByReference("batch1")
        .as(StepVerifier::create)
        .assertNext(
            savedBatch -> {
              assertThat(savedBatch).isEqualTo(expectedBatch);
              assertThat(savedBatch.getAllocations()).contains(orderLine);
            })
        .verifyComplete();
  }
}
