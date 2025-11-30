package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStockException;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.service.AllocationService;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DomainAllocationService implements AllocationService {

  public Mono<Batch> allocate(OrderLine line, Flux<Batch> batches) {
    return batches.filter(batch -> batch.canAllocate(line))
        .sort(Batch.ETA_COMPARATOR)
        .next()
        .doOnNext(batch -> batch.allocate(line))
        .switchIfEmpty(
            Mono.error(new OutOfStockException(line.sku(), line.orderId(), line.quantity())));
  }
}
