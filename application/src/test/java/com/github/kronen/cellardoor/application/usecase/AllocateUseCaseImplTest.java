package com.github.kronen.cellardoor.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.github.kronen.cellardoor.common.exceptions.InvalidSkuException;
import com.github.kronen.cellardoor.domain.allocation.DomainAllocationService;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.BatchRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AllocateUseCaseImplTest {

  @InjectMocks
  AllocateUseCaseImpl allocateUseCase;

  @Mock
  BatchRepository batchRepository;

  @Mock
  DomainAllocationService domainAllocationService;

  @Test
  void shouldReturnAllocation() {
    OrderLine line = OrderLine.builder()
        .orderId("o1")
        .sku("COMPLICATED-LAMP")
        .quantity(10)
        .build();
    Batch batch = Batch.builder()
        .reference("b1")
        .sku("COMPLICATED-LAMP")
        .purchasedQuantity(100)
        .build();
    given(batchRepository.findAll()).willReturn(Flux.just(batch));
    given(domainAllocationService.allocate(eq(line), any(Flux.class))).willReturn(Mono.just("b1"));

    StepVerifier.create(allocateUseCase.allocate(Mono.just(line)))
        .expectNext("b1")
        .verifyComplete();
  }

  @Test
  void shouldThrowExceptionForInvalidSku() {
    OrderLine line = OrderLine.builder()
        .orderId("o1")
        .sku("NONEXISTENTSKU") // SKU that doesn't match
        .quantity(10)
        .build();
    Batch batch = Batch.builder()
        .reference("b1")
        .sku("AREALSKU") // Different SKU
        .purchasedQuantity(100)
        .build();
    given(batchRepository.findAll()).willReturn(Flux.just(batch));

    StepVerifier.create(allocateUseCase.allocate(Mono.just(line)))
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(InvalidSkuException.class);
          assertThat(throwable.getMessage()).contains("NONEXISTENTSKU");
        })
        .verify();
  }
}
