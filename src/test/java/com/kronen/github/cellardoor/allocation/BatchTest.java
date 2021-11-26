package com.kronen.github.cellardoor.allocation;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class BatchTest {

    public void testAllocatingToABatchReducesTheAvailableQuantity() {
        Pair<Batch, OrderLine> allocation = makeBatchAndLine("SMALL-TABLE",2,10);
        Batch batch = allocation.getLeft();
        OrderLine line = allocation.getRight();

        batch.allocate(line);

        assertThat(batch.getAvailableQuantity()).isEqualTo(8);
    }

    public void testCanAllocateIfAvailableGreaterThanRequired() {
        Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP",20,2);
        Batch largeBatch = allocation.getLeft();
        OrderLine smallLine = allocation.getRight();

        assertThat(largeBatch.canAllocate(smallLine)).isTrue();
    }

    public void testCannotAllocateIfAvailableSmallerThanRequired() {
        Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP",2,20);
        Batch smallBatch = allocation.getLeft();
        OrderLine largeLine = allocation.getRight();

        assertThat(smallBatch.canAllocate(largeLine)).isFalse();
    }

    public void testCanAllocateIfAvailableEqualToRequired() {
        Pair<Batch, OrderLine> allocation = makeBatchAndLine("ELEGANT-LAMP",2,2);
        Batch batch = allocation.getLeft();
        OrderLine line = allocation.getRight();

        assertThat(batch.canAllocate(line)).isTrue();
    }

    public void testCannotAllocateIfSkusDoNotMatch() {
        Batch batch = new Batch("batch-001", "UNCOMFORTABLE-CHAIR",100, null);
        OrderLine differentSkuLine = new OrderLine("order-123", "EXPENSIVE-CHAIR", 10);

        assertThat(batch.canAllocate(differentSkuLine)).isFalse();
    }

    public void testPrefersWarehouseBatchesToShipments() {
        assert false;
    }

    public void testPrefersEarlierBatches() {
        assert false;
    }

    public Pair<Batch, OrderLine> makeBatchAndLine(String sku, Integer batchQty, Integer lineQty) {
        Batch batch = new Batch("batch-001", sku, batchQty, OffsetDateTime.now());
        OrderLine line = new OrderLine("order-123", sku, lineQty);

        return MutablePair.of(batch, line);
    }

}
