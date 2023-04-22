package com.github.kronen.cellardoor.system.database.orderline;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OrderLineRepository extends ReactiveCrudRepository<OrderLineEntity, BigInteger> {
}
