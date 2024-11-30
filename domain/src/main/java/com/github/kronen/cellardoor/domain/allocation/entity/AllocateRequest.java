package com.github.kronen.cellardoor.domain.allocation.entity;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a request to allocate an order line to a batch.
 */
@Data
@Builder
public class AllocateRequest {

  private OrderLine orderLine;
}
