package com.github.kronen.cellardoor.system.database.batch;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BatchRepository extends ReactiveCrudRepository<BatchEntity, String> {
}
