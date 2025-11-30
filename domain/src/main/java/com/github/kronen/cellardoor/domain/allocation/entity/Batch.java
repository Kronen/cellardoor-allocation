package com.github.kronen.cellardoor.domain.allocation.entity;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Batch {

  public static final Comparator<Batch> ETA_COMPARATOR =
      Comparator.comparing(Batch::getEta, Comparator.nullsFirst(Instant::compareTo));

  @NonNull @EqualsAndHashCode.Include
  private String reference;

  @NonNull private Integer purchasedQuantity;

  @NonNull @Builder.Default
  private Set<OrderLine> allocations = new HashSet<>();

  @NonNull private String sku;

  private Instant eta;

  public void allocate(OrderLine line) {
    if (canAllocate(line)) {
      allocations.add(line);
    }
  }

  public void deallocate(OrderLine line) {
    allocations.remove(line);
  }

  public Integer allocatedQuantity() {
    return this.allocations.stream().map(OrderLine::quantity).reduce(0, Integer::sum);
  }

  public Integer availableQuantity() {
    return purchasedQuantity - allocatedQuantity();
  }

  public boolean canAllocate(OrderLine line) {
    return sku.equals(line.sku()) && availableQuantity() >= line.quantity();
  }
}
