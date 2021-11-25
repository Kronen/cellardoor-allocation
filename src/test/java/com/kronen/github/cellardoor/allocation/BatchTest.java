package com.kronen.github.cellardoor.allocation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class BatchTest {

    public void testAllocatingToABatchReducesTheAvailableQuantity() {
        Batch batch = new Batch("batch-001", "SMALL-TABLE", 20, OffsetDateTime.now());
        OrderLine line = new OrderLine("order-ref", "SMALL-TABLE", 2);
        batch.allocate(line);

        assertThat(batch.getAvailableQuantity()).isEqualTo(18);
    }

    public void testCanAllocateIfAvailableGreaterThanRequired() {
        assert false;
    }

    public void testCannotAllocateIfAvailableSmallerThanRequired() {
        assert false;
    }

    public void testCanAllocateIfAvailableEqualToRequired() {
        assert false;
    }

    public void testPrefersWarehouseBatchesToShipments() {
        assert false;
    }

    public void testPrefersEarlierBatches() {
        assert false;
    }

}
