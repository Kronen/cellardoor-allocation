package com.github.kronen.cellardoor.mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AllocationMapper {

  AllocateRequest toDomain(AllocateRequestDTO allocateRequest);
}
