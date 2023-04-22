package com.github.kronen.cellardoor.domain.allocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Batch implements Comparable<Batch> {

    @EqualsAndHashCode.Include
    @NonNull
    private String reference;
    @NonNull
    private String sku;

    @NonNull
    private final Integer purchasedQuantity;

    private final Set<OrderLine> allocations = new HashSet<>();

    private OffsetDateTime eta;

    public void allocate(OrderLine line) {
        if (canAllocate(line)) {
            allocations.add(line);
        }
    }

    public void deallocate(OrderLine line) {
        allocations.remove(line);
    }

    public Integer allocatedQuantity() {
        return this.allocations.stream().map(OrderLine::getQuantity).reduce(0, Integer::sum);
    }

    public Integer availableQuantity() {
        return purchasedQuantity - allocatedQuantity();
    }

    public boolean canAllocate(OrderLine line) {
        return sku.equals(line.getSku()) && availableQuantity() >= line.getQuantity();
    }

    @Override
    public int compareTo(Batch o) {
        if (o.eta == null) {
            return 1;
        } else if (this.eta == null) {
            return -1;
        }
        return this.eta.isAfter(o.eta) ? 1 : this.eta.isEqual(o.eta) ? 0 : -1;
    }
}
