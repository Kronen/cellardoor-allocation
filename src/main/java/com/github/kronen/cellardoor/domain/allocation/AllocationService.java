package com.github.kronen.cellardoor.domain.allocation;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AllocationService {

    public Mono<String> allocate(OrderLine line, List<Batch> batches) throws OutOfStock;

}
