package com.github.kronen.cellardoor.system.database.batch;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MongoBatchRepository extends ReactiveMongoRepository<BatchDocument, ObjectId> {

    Mono<BatchDocument> findByReference(String batchReference);

}
