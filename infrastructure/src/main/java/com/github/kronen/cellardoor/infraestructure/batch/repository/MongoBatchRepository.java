package com.github.kronen.cellardoor.infraestructure.batch.repository;

import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MongoBatchRepository extends ReactiveMongoRepository<BatchDocument, String> {

  Mono<BatchDocument> findByReference(String batchReference);

  Flux<BatchDocument> findBySku(String sku);
}
