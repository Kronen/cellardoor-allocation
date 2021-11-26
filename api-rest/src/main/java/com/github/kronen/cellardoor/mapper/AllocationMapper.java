package com.github.kronen.cellardoor.mapper;

import org.mapstruct.Mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;

@Mapper
public interface AllocationMapper {

    AllocateRequest toDomain(AllocateRequestDTO allocateRequest);
}
