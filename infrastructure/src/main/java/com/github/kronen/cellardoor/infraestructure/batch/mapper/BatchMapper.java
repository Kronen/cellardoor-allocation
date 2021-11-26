package com.github.kronen.cellardoor.infraestructure.batch.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BatchMapper {

    Batch toBatch(BatchDocument Batch);

    BatchDocument toBatchDocument(Batch Batch);
}
