package com.github.kronen.cellardoor.integration;

import java.time.Instant;
import java.util.Set;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.BatchRepository;
import com.github.kronen.cellardoor.domain.allocation.port.OrderLineRepository;
import com.github.kronen.cellardoor.infraestructure.batch.mapper.BatchMapperImpl;
import com.github.kronen.cellardoor.infraestructure.config.mongo.MongoConfig;
import com.github.kronen.cellardoor.infraestructure.orderline.mapper.OrderLineMapperImpl;
import com.github.kronen.cellardoor.infraestructure.orderline.repository.OrderLineRepositoryImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest(
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              MongoConfig.class,
              BatchRepository.class,
              OrderLineRepositoryImpl.class,
              BatchMapperImpl.class,
              OrderLineMapperImpl.class
            }))
class AllocationRepositoriesTestIT {

  @Autowired
  OrderLineRepository orderLineRepository;

  @Autowired
  BatchRepository batchRepository;

  OrderLine buildOrderLine(String sku, Integer quantity, String orderId) {
    return OrderLine.builder().orderId(orderId).sku(sku).quantity(quantity).build();
  }

  OrderLine insertOrderLine(String sku, Integer quantity, String orderId) {
    return orderLineRepository.save(buildOrderLine(sku, quantity, orderId)).block();
  }

  Batch insertBatch(String reference, String sku, Set<OrderLine> orderLines) {
    return this.batchRepository
        .save(Batch.builder()
            .reference(reference)
            .sku(sku)
            .allocations(orderLines)
            .purchasedQuantity(12)
            .build())
        .block();
  }

  @Test
  void testWhenInsert3Then3AreExpected() {
    orderLineRepository
        .deleteAll()
        .thenMany(orderLineRepository.saveAll(Flux.just(
            buildOrderLine("GENERIC-SOFA", 12, "order1"),
            buildOrderLine("RED-TABLE", 13, "order1"),
            buildOrderLine("GENERIC-SOFA", 14, "order2"))))
        .as(StepVerifier::create)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void testWhenDeleteAllThenNoOrderLinesExpected() {
    insertOrderLine("GENERIC-SOFA", 12, "order1");

    orderLineRepository
        .deleteAll()
        .thenMany(orderLineRepository.findAll())
        .as(StepVerifier::create)
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void testOrderLineRepositoryCanSaveALine() {
    orderLineRepository
        .save(OrderLine.builder()
            .orderId("order1")
            .sku("RED-CHAIR")
            .quantity(12)
            .build())
        .as(StepVerifier::create)
        .assertNext(savedLine -> Assertions.assertThat(savedLine.id()).isNotNull())
        .verifyComplete();
  }

  @Test
  void testBatchRepositoryCanSaveABatch() {
    var batch = Batch.builder()
        .reference("batch1")
        .sku("RUSTY-SOAPDISH")
        .purchasedQuantity(100)
        .eta(Instant.now())
        .build();

    this.batchRepository
        .save(batch)
        .as(StepVerifier::create)
        .assertNext(savedBatch -> {
          Assertions.assertThat(savedBatch).isEqualTo(batch); // Only compares reference
          Assertions.assertThat(savedBatch.getSku()).isEqualTo(batch.getSku());
          Assertions.assertThat(savedBatch.getPurchasedQuantity()).isEqualTo(batch.getPurchasedQuantity());
          Assertions.assertThat(savedBatch.getEta()).isEqualTo(batch.getEta());
        })
        .verifyComplete();
  }

  @Test
  void testRepositoryCanRetrieveABatchWithAllocations() {
    var batchRef = "batch1";
    var orderLine = insertOrderLine("GENERIC-SOFA", 12, "order1");
    var expectedBatch = insertBatch(batchRef, "GENERIC-SOFA", Set.of(orderLine));

    batchRepository
        .findByReference(batchRef)
        .as(StepVerifier::create)
        .assertNext(savedBatch -> {
          Assertions.assertThat(savedBatch).isEqualTo(expectedBatch);
          Assertions.assertThat(savedBatch.getAllocations()).contains(orderLine);
        })
        .verifyComplete();
  }
}
