package com.github.kronen.cellardoor.system.repository.batch;

import com.github.kronen.cellardoor.domain.allocation.Batch;
import com.github.kronen.cellardoor.system.database.batch.BatchMapper;
import com.github.kronen.cellardoor.system.database.batch.MongoBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BatchRepositoryImpl implements BatchRepository {

    private final MongoBatchRepository repository;

    private final BatchMapper mapper;

    @Override
    public Flux<Batch> findAll() {
        return repository.findAll().map(mapper::toBatch);
    }

    @Override
    public Mono<Batch> findById(String batchReference) {
        return repository.findByReference(batchReference).map(mapper::toBatch);
    }

}
