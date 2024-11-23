package com.github.kronen.cellardoor.domain.allocation.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllocateRequest {

    private OrderLine orderLine;

}
