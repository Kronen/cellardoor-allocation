package com.github.kronen.cellardoor.application.usecase;

import java.util.List;

import com.github.kronen.cellardoor.common.exceptions.InvalidSkuException;
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
    return orderLineMono.flatMap(
        orderLine -> batchRepository.findAll().collectList().flatMap(batches -> {
          if (!isValidSku(orderLine.getSku(), batches)) {
            return Mono.error(new InvalidSkuException(orderLine.getSku()));
          }

          return domainAllocationService.allocate(orderLine, Flux.fromIterable(batches));
        }));
  }

  @Override
  public Mono<Batch> getBatch(String reference) {
    return batchRepository.findByReference(reference);
  }

  private boolean isValidSku(String sku, List<Batch> batches) {
    return batches.stream().anyMatch(batch -> batch.getSku().equals(sku)); // SKU validation logic
  }
}
