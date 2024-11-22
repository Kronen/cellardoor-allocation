package com.github.kronen.cellardoor.system.database.orderline;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoOrderLineRepository
    extends ReactiveMongoRepository<OrderLineDocument, String> {}
