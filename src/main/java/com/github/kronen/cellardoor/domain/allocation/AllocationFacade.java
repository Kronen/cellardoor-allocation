package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import com.github.kronen.cellardoor.model.AllocateRequest;
import com.github.kronen.cellardoor.system.repository.batch.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AllocationFacade {

  private final AllocationService allocationService;

  private final BatchRepository batchRepository;

  public Mono<String> allocate(Mono<AllocateRequest> allocateRequestMono) {
    return allocateRequestMono.flatMap(
      allocateRequest -> allocationService.allocate(allocateRequest.getOrderLine(), batchRepository.findAll())
        .onErrorResume(OutOfStock.class, ex -> Mono.empty()));
  }

  public Mono<Batch> getBatch(String reference) {
    return batchRepository.findByReference(reference);
  }

}
