package com.github.kronen.cellardoor.system.router;

import com.github.kronen.cellardoor.common.exceptions.OutOfStock;
import com.github.kronen.cellardoor.domain.allocation.AllocationService;
import com.github.kronen.cellardoor.domain.allocation.Batch;
import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import com.github.kronen.cellardoor.system.database.batch.BatchMapper;
import com.github.kronen.cellardoor.system.database.batch.BatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationHandler {

    private final AllocationService allocationService;

    private final BatchRepository batchRepository;

    private final BatchMapper batchMapper;

    /**
     * Allocation
     *
     * @param request
     *
     * @return
     */
    public Mono<ServerResponse> allocate(ServerRequest request) {
        Mono<List<Batch>> batchesMono = batchRepository.findAll().map(batchMapper::batchEntityToBatch).collectList();

        Mono<OrderLine> orderLineMono = request.bodyToMono(OrderLine.class);

        Mono<String> batchMono = Mono.zip(orderLineMono, batchesMono).flatMap(o -> {
            try {
                return allocationService.allocate(o.getT1(), o.getT2());
            } catch (OutOfStock e) {
                return Mono.empty();
            }
        });

        return batchMono.flatMap(reference -> ServerResponse.created(URI.create("batch/" + reference))
                .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reference)));
    }

    /**
     * Allocation
     *
     * @param request
     *
     * @return
     */
    public Mono<ServerResponse> getBatch(ServerRequest request) {
        Mono<Batch> batch = batchRepository.findById(request.pathVariable("batch_reference"))
                .map(batchMapper::batchEntityToBatch);
        return ServerResponse.ok().body(BodyInserters.fromPublisher(batch, Batch.class));
    }

}
