package com.github.kronen.cellardoor.domain.allocation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AllocationService {

  /**
   * Allocates an order line to an available batch.
   *
   * @param line    The order line to allocate.
   * @param batches The available batches to choose from.
   * @return A Mono that emits the reference of the allocated batch.
   */
  Mono<String> allocate(OrderLine line, Flux<Batch> batches);

}
