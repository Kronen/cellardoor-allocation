package com.github.kronen.cellardoor.controller;

import java.net.URI;

import com.github.kronen.cellardoor.api.AllocationApi;
import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.usecase.AllocateUseCase;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;
import com.github.kronen.cellardoor.mapper.AllocationMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AllocationController implements AllocationApi {

  private final AllocateUseCase allocateUseCase;

  private final AllocationMapper mapper;

  @Override
  public Mono<ResponseEntity<String>> allocate(
      @Valid @RequestBody final Mono<AllocateRequestDTO> allocateRequestDto, final ServerWebExchange exchange) {
    return allocateUseCase
        .allocate(allocateRequestDto.map(mapper::toDomain).map(AllocateRequest::getOrderLine))
        .switchIfEmpty(Mono.error(new IllegalStateException("Allocation failed: no reference produced")))
        .doOnNext(batchRef -> log.info("Allocated reference: {}", batchRef))
        .map(batchRef -> ResponseEntity.created(URI.create("allocation/" + batchRef))
            .body(batchRef));
  }

  @Override
  public Mono<ResponseEntity<Batch>> getBatch(final String batchReference, final ServerWebExchange exchange) {
    return allocateUseCase.getBatch(batchReference).map(ResponseEntity::ok);
  }
}
