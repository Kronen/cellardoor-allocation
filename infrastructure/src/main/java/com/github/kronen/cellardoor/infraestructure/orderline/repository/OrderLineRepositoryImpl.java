package com.github.kronen.cellardoor.infraestructure.orderline.repository;

import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.OrderLineRepository;
import com.github.kronen.cellardoor.infraestructure.orderline.mapper.OrderLineMapper;

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
