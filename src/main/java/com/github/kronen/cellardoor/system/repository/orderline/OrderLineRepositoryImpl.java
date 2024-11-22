package com.github.kronen.cellardoor.system.repository.orderline;

import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import com.github.kronen.cellardoor.system.database.orderline.MongoOrderLineRepository;
import com.github.kronen.cellardoor.system.database.orderline.OrderLineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderLineRepositoryImpl implements OrderLineRepository {

  private final MongoOrderLineRepository repository;

  private final OrderLineMapper mapper;

  @Override
  public Flux<OrderLine> findAll() {
    return repository.findAll().map(mapper::toOrderLine);
  }

  @Override
  public Flux<OrderLine> saveAll(Flux<OrderLine> orderLines) {
    return repository.saveAll(orderLines.map(mapper::toOrderLineDocument)).map(mapper::toOrderLine);
  }

  @Override
  public Mono<OrderLine> save(OrderLine orderLine) {
    return repository.save(mapper.toOrderLineDocument(orderLine)).map(mapper::toOrderLine);
  }

  @Override
  public Mono<Void> deleteAll() {
    return repository.deleteAll();
  }
}
