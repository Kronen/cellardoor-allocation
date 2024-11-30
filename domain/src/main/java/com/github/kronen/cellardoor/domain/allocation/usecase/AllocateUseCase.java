package com.github.kronen.cellardoor.domain.allocation.usecase;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;

import reactor.core.publisher.Mono;

public interface AllocateUseCase {

  Mono<String> allocate(Mono<OrderLine> line);

  Mono<Batch> getBatch(String reference);

  Mono<Batch> createBatch(Batch batch);
}
