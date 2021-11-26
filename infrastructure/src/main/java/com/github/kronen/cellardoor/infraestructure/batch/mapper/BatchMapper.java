package com.github.kronen.cellardoor.infraestructure.batch.mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BatchMapper {

    Batch toBatch(BatchDocument batch);

    BatchDocument toBatchDocument(Batch batch);
}
