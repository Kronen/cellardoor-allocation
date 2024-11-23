package com.github.kronen.cellardoor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.github.kronen.cellardoor.api.AllocationApi;
import com.github.kronen.cellardoor.domain.allocation.AllocationUseCase;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;
import com.github.kronen.cellardoor.mapper.AllocationMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AllocationController implements AllocationApi {

    private final AllocationUseCase allocationUseCase;

    private final AllocationMapper mapper;

    @Override
    public Mono<ResponseEntity<String>> allocate(
            @Valid @RequestBody Mono<AllocateRequestDTO> allocateRequestDTO, final ServerWebExchange exchange) {
        return allocationUseCase
                .allocate(allocateRequestDTO.map(mapper::toDomain))
                .map(reference -> ResponseEntity.ok().body(reference));
    }

    @Override
    public Mono<ResponseEntity<Batch>> getBatch(
            @PathVariable("batch_reference") String batchReference, final ServerWebExchange exchange) {
        return allocationUseCase.getBatch(batchReference).map(ResponseEntity::ok);
    }
}
