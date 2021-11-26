package com.github.kronen.cellardoor.infraestructure.orderline.repository;

import com.github.kronen.cellardoor.infraestructure.orderline.entity.OrderLineDocument;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoOrderLineRepository extends ReactiveMongoRepository<OrderLineDocument, String> {}
