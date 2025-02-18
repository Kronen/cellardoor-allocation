package com.github.kronen.cellardoor.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;

import com.github.kronen.cellardoor.domain.allocation.entity.AllocateRequest;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.rest.dto.AllocateRequestDTO;
import com.github.kronen.rest.dto.NewBatchDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface AllocationMapper {

  AllocateRequest toDomain(AllocateRequestDTO allocateRequest);

  @Mapping(target = "eta", source = "arrivalAt", qualifiedByName = "offsetDateTimeToInstant")
  Batch toDomain(NewBatchDTO newBatchDTO);

  @Named("offsetDateTimeToInstant")
  default Instant map(OffsetDateTime value) {
    if (value != null) {
      return value.toInstant();
    } else {
      return null;
    }
  }
}
