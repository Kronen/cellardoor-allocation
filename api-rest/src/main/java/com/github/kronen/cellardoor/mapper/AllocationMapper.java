package com.github.kronen.cellardoor.mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;
import com.github.kronen.cellardoor.dto.NewBatchDTO;

import org.mapstruct.Mapper;

@Mapper
public interface AllocationMapper {

  AllocateRequest toDomain(AllocateRequestDTO allocateRequest);

  Batch toDomain(NewBatchDTO newBatchDTO);
}
