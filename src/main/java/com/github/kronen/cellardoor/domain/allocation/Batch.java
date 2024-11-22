package com.github.kronen.cellardoor.domain.allocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Batch implements Comparable<Batch> {

  @NonNull @EqualsAndHashCode.Include private String reference;

  @NonNull private Integer purchasedQuantity;

  @Builder.Default private Set<OrderLine> allocations = new HashSet<>();

  @NonNull private String sku;

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

  public int compareTo(Batch o) {
    return Comparator.comparing(Batch::getEta, Comparator.nullsFirst(OffsetDateTime::compareTo))
        .compare(this, o);
  }
}
