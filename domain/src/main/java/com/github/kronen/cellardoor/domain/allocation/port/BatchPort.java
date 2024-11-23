package com.github.kronen.cellardoor.domain.allocation.port;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BatchPort {

  Flux<Batch> findAll();

  Mono<Batch> findByReference(String batchReference);

  Mono<Batch> save(Batch batch);

  Flux<Batch> saveAll(Flux<Batch> batches);
}
