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

  private final MongoBatchRepository batchRepository;

  private final BatchMapper mapper;

  @Override
  public Flux<Batch> findAll() {
    return batchRepository.findAll()
      .map(mapper::toBatch);
  }

  @Override
  public Mono<Batch> findByReference(String batchReference) {
    return batchRepository.findByReference(batchReference)
      .map(mapper::toBatch);
  }

  @Override
  public Mono<Batch> save(Batch batch) {
    return batchRepository.save(mapper.toBatchDocument(batch))
      .map(mapper::toBatch);
  }

  @Override
  public Flux<Batch> saveAll(Flux<Batch> batches) {
    return batchRepository.saveAll(batches.map(mapper::toBatchDocument)).map(mapper::toBatch);
  }

}
