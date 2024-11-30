package com.github.kronen.cellardoor.infraestructure.batch.repository;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.port.BatchRepository;
import com.github.kronen.cellardoor.infraestructure.batch.mapper.BatchMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BatchRepositoryImpl implements BatchRepository {

  private final MongoBatchRepository mongoRepository;

  private final BatchMapper mapper;

  @Override
  public Flux<Batch> findAll() {
    return mongoRepository.findAll().map(mapper::toBatch);
  }

  @Override
  public Mono<Batch> findByReference(String batchReference) {
    return mongoRepository.findByReference(batchReference).map(mapper::toBatch);
  }

  @Override
  public Flux<Batch> findBySku(String sku) {
    return mongoRepository.findBySku(sku).map(mapper::toBatch);
  }

  @Override
  public Mono<Batch> save(Batch batch) {
    return mongoRepository.save(mapper.toBatchDocument(batch)).map(mapper::toBatch);
  }

  @Override
  public Flux<Batch> saveAll(Flux<Batch> batches) {
    return mongoRepository.saveAll(batches.map(mapper::toBatchDocument)).map(mapper::toBatch);
  }
}
