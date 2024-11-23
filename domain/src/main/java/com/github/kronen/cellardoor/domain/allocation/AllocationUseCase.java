package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStockException;
import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.port.BatchPort;
import com.github.kronen.cellardoor.domain.allocation.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AllocationUseCase {

    private final AllocationService allocationService;

    private final BatchPort batchPort;

    public Mono<String> allocate(Mono<AllocateRequest> allocateRequestMono) {
        return allocateRequestMono.flatMap(allocateRequest -> allocationService
                .allocate(allocateRequest.getOrderLine(), batchPort.findAll())
                .onErrorResume(OutOfStockException.class, ex -> Mono.empty()));
    }

    public Mono<Batch> getBatch(String reference) {
        return batchPort.findByReference(reference);
    }
}
