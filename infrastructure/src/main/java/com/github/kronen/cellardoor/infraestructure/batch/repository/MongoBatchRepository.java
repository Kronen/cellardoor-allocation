package com.github.kronen.cellardoor.infraestructure.batch.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

import reactor.core.publisher.Mono;

@Repository
public interface MongoBatchRepository extends ReactiveMongoRepository<BatchDocument, String> {

    Mono<BatchDocument> findByReference(String batchReference);
}
