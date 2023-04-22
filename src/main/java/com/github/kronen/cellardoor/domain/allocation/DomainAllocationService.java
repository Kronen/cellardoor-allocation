package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class DomainAllocationService implements AllocationService {

    public Mono<String> allocate(OrderLine line, List<Batch> batches) throws OutOfStock {
        Optional<Batch> batch = batches.stream().sorted().filter(b -> b.canAllocate(line)).findFirst();
        return batch.map(b -> {
            b.allocate(line);
            return Mono.just(b.getReference());
        }).orElseThrow(() -> new OutOfStock(OutOfStock.NO_AVAILABLE_BATCH + line.getSku()));
    }
}
