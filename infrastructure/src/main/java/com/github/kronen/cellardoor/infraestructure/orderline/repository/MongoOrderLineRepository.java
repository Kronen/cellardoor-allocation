package com.github.kronen.cellardoor.infraestructure.orderline.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.github.kronen.cellardoor.infraestructure.orderline.entity.OrderLineDocument;

@Repository
public interface MongoOrderLineRepository extends ReactiveMongoRepository<OrderLineDocument, String> {}
