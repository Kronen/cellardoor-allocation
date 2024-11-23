package com.github.kronen.cellardoor.domain.allocation.port;

import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderLineRepository {

  Flux<OrderLine> findAll();

  Flux<OrderLine> saveAll(Flux<OrderLine> orderLines);

  Mono<OrderLine> save(OrderLine orderLine);

  Mono<Void> deleteAll();

}
