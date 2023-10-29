package com.github.kronen.cellardoor.system.repository.orderline;

import com.github.kronen.cellardoor.system.database.orderline.MongoOrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderLineRepositoryImpl implements OrderLineRepository {

    private final MongoOrderLineRepository repository;

}
