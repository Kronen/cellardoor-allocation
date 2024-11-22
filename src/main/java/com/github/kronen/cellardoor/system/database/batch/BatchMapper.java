package com.github.kronen.cellardoor.system.database.batch;

import com.github.kronen.cellardoor.domain.allocation.Batch;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BatchMapper {

  Batch toBatch(BatchDocument Batch);

  BatchDocument toBatchDocument(Batch Batch);
}
