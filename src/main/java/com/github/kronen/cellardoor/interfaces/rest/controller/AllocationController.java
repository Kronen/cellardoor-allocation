package com.github.kronen.cellardoor.interfaces.rest.controller;

import com.github.kronen.cellardoor.api.AllocationApi;
import com.github.kronen.cellardoor.domain.allocation.AllocationFacade;
import com.github.kronen.cellardoor.domain.allocation.Batch;
import com.github.kronen.cellardoor.model.AllocateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AllocationController implements AllocationApi {

    private final AllocationFacade allocationFacade;

    @Override
    public Mono<ResponseEntity<String>> allocate(@Valid @RequestBody Mono<AllocateRequest> allocateRequest,
            final ServerWebExchange exchange) {
        return allocationFacade.allocate(allocateRequest).map(reference -> ResponseEntity.ok().body(reference));
    }

    @Override
    public Mono<ResponseEntity<Batch>> getBatch(@PathVariable("batch_reference") String batchReference,
            final ServerWebExchange exchange) {
        return allocationFacade.getBatch(batchReference).map(ResponseEntity::ok);
    }
}
