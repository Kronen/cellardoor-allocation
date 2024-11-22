package com.github.kronen.cellardoor.system.repository.batch;

import com.github.kronen.cellardoor.domain.allocation.Batch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BatchRepository {

  Flux<Batch> findAll();

  Mono<Batch> findByReference(String batchReference);

  Mono<Batch> save(Batch batch);

  Flux<Batch> saveAll(Flux<Batch> batches);
}
