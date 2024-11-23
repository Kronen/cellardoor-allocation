package com.github.kronen.cellardoor.domain.allocation;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@Import(DomainAllocationService.class)
public class DomainAllocationServiceTest {

  com.github.kronen.cellardoor.domain.allocation.service.AllocationService allocationService =
      new DomainAllocationService();

  private Pair<Batch, OrderLine> makeBatchAndLine(String sku, Integer batchQty, Integer lineQty) {
    Batch batch =
        Batch.builder()
            .reference("batch-001")
            .sku(sku)
            .purchasedQuantity(batchQty)
            .eta(OffsetDateTime.now())
            .build();
    OrderLine line = OrderLine.builder().orderId("order-123").sku(sku).quantity(lineQty).build();

    return MutablePair.of(batch, line);
  }

  @Test
  public void testAllocatingToABatchReducesTheAvailableQuantity() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("SMALL-TABLE", 10, 2);
    Batch batch = allocation.getLeft();
    OrderLine line = allocation.getRight();

    batch.allocate(line);

    assertThat(batch.availableQuantity()).isEqualTo(8);
  }

  @Test
  public void testCanAllocateIfAvailableGreaterThanRequired() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP", 20, 2);
    Batch largeBatch = allocation.getLeft();
    OrderLine smallLine = allocation.getRight();

    assertThat(largeBatch.canAllocate(smallLine)).isTrue();
  }

  @Test
  public void testCannotAllocateIfAvailableSmallerThanRequired() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP", 2, 20);
    Batch smallBatch = allocation.getLeft();
    OrderLine largeLine = allocation.getRight();

    assertThat(smallBatch.canAllocate(largeLine)).isFalse();
  }

  @Test
  public void testCanAllocateIfAvailableEqualToRequired() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP", 2, 2);
    Batch batch = allocation.getLeft();
    OrderLine line = allocation.getRight();

    assertThat(batch.canAllocate(line)).isTrue();
  }

  @Test
  public void testCannotAllocateIfSkusDoNotMatch() {
    Batch batch =
        Batch.builder()
            .reference("batch-001")
            .sku("UNCOMFORTABLE-CHAIR")
            .purchasedQuantity(100)
            .build();
    OrderLine differentSkuLine =
        OrderLine.builder().orderId("order-123").sku("EXPENSIVE-CHAIR").quantity(10).build();

    assertThat(batch.canAllocate(differentSkuLine)).isFalse();
  }

  @Test
  public void testCanOnlyDeallocateAllocatedLines() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("DECORATIVE-TRINKET", 20, 2);
    Batch batch = allocation.getLeft();
    OrderLine unallocatedLine = allocation.getRight();

    batch.deallocate(unallocatedLine);

    assertThat(batch.availableQuantity()).isEqualTo(20);
  }

  @Test
  public void testAllocationIsIdempotent() {
    Pair<Batch, OrderLine> allocation = makeBatchAndLine("DECORATIVE-TRINKET", 20, 2);
    Batch batch = allocation.getLeft();
    OrderLine line = allocation.getRight();

    batch.allocate(line);
    batch.allocate(line);

    assertThat(batch.availableQuantity()).isEqualTo(18);
  }

  @Test
  public void testPrefersWarehouseBatchesToShipments() {
    Batch warehouseBatch =
        Batch.builder()
            .reference("warehouse-batch1")
            .sku("RETRO-CLOCK")
            .purchasedQuantity(100)
            .build();

    Batch shipmentBatch =
        Batch.builder()
            .reference("shipment-batch")
            .sku("RETRO-CLOCK")
            .purchasedQuantity(100)
            .eta(OffsetDateTime.now(ZoneOffset.UTC))
            .build();

    OrderLine line = OrderLine.builder().orderId("oref").sku("RETRO-CLOCK").quantity(10).build();

    StepVerifier.create(allocationService.allocate(line, Flux.just(warehouseBatch, shipmentBatch)))
        .expectNext(warehouseBatch.getReference())
        .verifyComplete();

    assertThat(warehouseBatch.availableQuantity()).isEqualTo(90);
    assertThat(shipmentBatch.availableQuantity()).isEqualTo(100);
  }

  @Test
  public void testPrefersEarlierBatches() {
    Batch earliestBatch =
        Batch.builder()
            .reference("warehouse-batch")
            .sku("MINIMALIST-SPOON")
            .purchasedQuantity(100)
            .eta(OffsetDateTime.now(ZoneOffset.UTC))
            .build();
    Batch mediumBatch =
        Batch.builder()
            .reference("warehouse-batch")
            .sku("MINIMALIST-SPOON")
            .purchasedQuantity(100)
            .eta(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1))
            .build();
    Batch latestBatch =
        Batch.builder()
            .reference("warehouse-batch")
            .sku("MINIMALIST-SPOON")
            .purchasedQuantity(100)
            .eta(OffsetDateTime.now(ZoneOffset.UTC).plusDays(10))
            .build();
    OrderLine line =
        OrderLine.builder().orderId("order1").sku("MINIMALIST-SPOON").quantity(10).build();

    StepVerifier.create(
            allocationService.allocate(line, Flux.just(mediumBatch, earliestBatch, latestBatch)))
        .expectNext(earliestBatch.getReference())
        .verifyComplete();

    assertThat(earliestBatch.availableQuantity()).isEqualTo(90);
    assertThat(mediumBatch.availableQuantity()).isEqualTo(100);
    assertThat(latestBatch.availableQuantity()).isEqualTo(100);
  }

  @Test
  public void testReturnsAllocatedBatchRef() {
    Batch warehouseBatch =
        Batch.builder()
            .reference("warehouse-batch")
            .sku("HIGHBROW-POSTER")
            .purchasedQuantity(100)
            .build();
    Batch shipmentBatch =
        Batch.builder()
            .reference("warehouse-batch")
            .sku("HIGHBROW-POSTER")
            .purchasedQuantity(100)
            .build();
    OrderLine line =
        OrderLine.builder().orderId("ord-ref").sku("HIGHBROW-POSTER").quantity(10).build();

    StepVerifier.create(allocationService.allocate(line, Flux.just(warehouseBatch, shipmentBatch)))
        .expectNext(warehouseBatch.getReference())
        .verifyComplete();
  }

  @Test
  public void testRaisesOutOfStockExceptionIfCannotAllocate() {
    Batch batch =
        Batch.builder()
            .reference("batch1")
            .sku("SMALL-FORK")
            .purchasedQuantity(10)
            .eta(OffsetDateTime.now())
            .build();
    OrderLine line1 = OrderLine.builder().orderId("order1").sku("SMALL-FORK").quantity(10).build();
    OrderLine line2 = OrderLine.builder().orderId("order2").sku("SMALL-FORK").quantity(1).build();

    Flux<Batch> batches = Flux.just(batch);
    allocationService.allocate(line1, batches).block();

    StepVerifier.create(allocationService.allocate(line2, batches))
        .expectError(OutOfStock.class)
        .verify();
  }
}
