package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineEntity;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineMapper;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineMapperImpl;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Import(DomainAllocationService.class)
public class AllocationTest {

    @Autowired
    OrderLineRepository orderLineRepository;

    @Autowired
    AllocationService allocationService;

    OrderLineMapper orderLineMapper = new OrderLineMapperImpl();

    private Pair<Batch, OrderLine> makeBatchAndLine(String sku, Integer batchQty, Integer lineQty) {
        Batch batch = Batch.builder().reference("batch-001").sku(sku).purchasedQuantity(batchQty)
                .eta(OffsetDateTime.now()).build();
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
        Batch batch = Batch.builder().reference("batch-001").sku("UNCOMFORTABLE-CHAIR").purchasedQuantity(100).build();
        OrderLine differentSkuLine = OrderLine.builder().orderId("order-123").sku("EXPENSIVE-CHAIR").quantity(10)
                .build();

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
    public void testPrefersWarehouseBatchesToShipments() throws OutOfStock {
        Batch warehouseBatch = Batch.builder().reference("warehouse-batch1").sku("RETRO-CLOCK").purchasedQuantity(100)
                .build();

        Batch shipmentBatch = Batch.builder().reference("shipment-batch").sku("RETRO-CLOCK").purchasedQuantity(100)
                .eta(OffsetDateTime.now(ZoneOffset.UTC)).build();

        OrderLine line = OrderLine.builder().orderId("oref").sku("RETRO-CLOCK").quantity(10).build();

        String reference = allocationService.allocate(line, Arrays.asList(warehouseBatch, shipmentBatch)).block();

        assertThat(warehouseBatch.availableQuantity()).isEqualTo(90);
        assertThat(shipmentBatch.availableQuantity()).isEqualTo(100);
    }

    @Test
    public void testPrefersEarlierBatches() throws OutOfStock {
        Batch earliestBatch = Batch.builder().reference("warehouse-batch").sku("MINIMALIST-SPOON")
                .purchasedQuantity(100).eta(OffsetDateTime.now(ZoneOffset.UTC)).build();
        Batch mediumBatch = Batch.builder().reference("warehouse-batch").sku("MINIMALIST-SPOON").purchasedQuantity(100)
                .eta(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)).build();
        Batch latestBatch = Batch.builder().reference("warehouse-batch").sku("MINIMALIST-SPOON").purchasedQuantity(100)
                .eta(OffsetDateTime.now(ZoneOffset.UTC).plusDays(10)).build();
        OrderLine line = OrderLine.builder().orderId("order1").sku("MINIMALIST-SPOON").quantity(10).build();

        allocationService.allocate(line, Arrays.asList(mediumBatch, earliestBatch, latestBatch));

        assertThat(earliestBatch.availableQuantity()).isEqualTo(90);
        assertThat(mediumBatch.availableQuantity()).isEqualTo(100);
        assertThat(latestBatch.availableQuantity()).isEqualTo(100);
    }

    @Test
    public void testReturnsAllocatedBatchRef() throws OutOfStock {
        Batch warehouseBatch = Batch.builder().reference("warehouse-batch").sku("HIGHBROW-POSTER")
                .purchasedQuantity(100).build();

        Batch shipmentBatch = Batch.builder().reference("warehouse-batch").sku("HIGHBROW-POSTER").purchasedQuantity(100)
                .build();

        OrderLine line = OrderLine.builder().orderId("oref").sku("HIGHBROW-POSTER").quantity(10).build();

        String reference = allocationService.allocate(line, Arrays.asList(warehouseBatch, shipmentBatch)).block();
        assertThat(reference).isEqualTo(warehouseBatch.getReference());
    }

    @Test
    public void testRaisesOutOfStockExceptionIfCaannotAllocate() throws OutOfStock {
        Batch batch = Batch.builder().reference("batch1").sku("SMALL-FORK").purchasedQuantity(10)
                .eta(OffsetDateTime.now()).build();
        OrderLine line = OrderLine.builder().orderId("order1").sku("SMALL-FORK").quantity(10).build();

        List<Batch> batches = Arrays.asList(batch);
        allocationService.allocate(line, batches);

        assertThrows(OutOfStock.class, () -> {
            allocationService.allocate(new OrderLine("order2", "SMALL-FORK", 1), batches);
        });
    }

    @Nested
    class AllocationDatabaseTest {

        private void insertOrderLines() {
            Flux<OrderLineEntity> orderLineFlux = Flux.just(
                    OrderLineEntity.builder().orderId("order1").sku("RED-CHAIR").quantity(12).build(),
                    OrderLineEntity.builder().orderId("order2").sku("RED-TABLE").quantity(13).build(),
                    OrderLineEntity.builder().orderId("order3").sku("BLUE-LIPSTICK").quantity(14).build());

            orderLineRepository.saveAll(orderLineFlux).subscribe();
        }

        @Test
        public void whenDeleteAll_then0IsExpected() {
            orderLineRepository.deleteAll().as(StepVerifier::create).expectNextCount(0).verifyComplete();
        }

        @Test
        public void whenInsert3_then3AreExpected() {
            insertOrderLines();
            orderLineRepository.findAll().as(StepVerifier::create).expectNextCount(3).verifyComplete();
        }

        @Test
        public void testOrderLineRepositoryCanSaveLines() {
            OrderLine orderLine = OrderLine.builder().orderId("order1").sku("RED-CHAIR").quantity(12).build();
            Mono<OrderLineEntity> lineMono = orderLineRepository
                    .save(orderLineMapper.orderLineToOrderLineEntity(orderLine));

            StepVerifier.create(lineMono).assertNext(line -> assertThat(line.getId()).isNotNull()).verifyComplete();
        }

    }

}
