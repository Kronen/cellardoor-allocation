package com.github.kronen.cellardoor.domain.allocation;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.service.AllocationService;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DomainAllocationService implements AllocationService {

    private static Function<Batch, Mono<? extends @NonNull String>> allocateOrderLine(OrderLine line) {
        return batch -> {
            batch.allocate(line);
            return Mono.just(batch.getReference());
        };
    }

    public Mono<String> allocate(OrderLine line, Flux<Batch> batches) {
        return batches.filter(b -> b.canAllocate(line))
                .sort()
                .next()
                .flatMap(allocateOrderLine(line))
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new OutOfStock(OutOfStock.BATCH_UNAVAILABLE + line.getSku()))));
    }
}
