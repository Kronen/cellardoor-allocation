package com.github.kronen.cellardoor.application.usecase;

import com.github.kronen.cellardoor.common.exceptions.InvalidSkuException;
import com.github.kronen.cellardoor.common.exceptions.OutOfStockException;
import com.github.kronen.cellardoor.domain.allocation.DomainAllocationService;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.BatchRepository;
import com.github.kronen.cellardoor.domain.allocation.usecase.AllocateUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AllocateUseCaseImpl implements AllocateUseCase {

  private final BatchRepository batchRepository;

  private final DomainAllocationService domainAllocationService;

  @Override
  public Mono<String> allocate(Mono<OrderLine> orderLineMono) {
    return orderLineMono.flatMap(orderLine -> batchRepository
        .findBySku(orderLine.getSku())
        .collectList()
        .flatMap(batches -> {
          if (batches.isEmpty()) {
            return Mono.error(new InvalidSkuException(orderLine.getSku()));
          }
          return domainAllocationService
              .allocate(orderLine, Flux.fromIterable(batches))
              .flatMap(batchRepository::save)
              .map(Batch::getReference)
              .switchIfEmpty(Mono.error(new OutOfStockException(
                  orderLine.getSku(), orderLine.getOrderId(), orderLine.getQuantity())));
        }));
  }

  @Override
  public Mono<Batch> getBatch(String reference) {
    return batchRepository.findByReference(reference);
  }

  @Override
  public Mono<Batch> createBatch(Batch batch) {
    return batchRepository.save(batch);
  }
}
