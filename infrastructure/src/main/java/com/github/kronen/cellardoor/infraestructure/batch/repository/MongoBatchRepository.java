package com.github.kronen.cellardoor.infraestructure.batch.repository;

import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MongoBatchRepository extends ReactiveMongoRepository<BatchDocument, String> {

    Mono<BatchDocument> findByReference(String batchReference);
}
