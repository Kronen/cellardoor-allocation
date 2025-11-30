package com.github.kronen.cellardoor.domain.allocation.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderLine(  String id,  String orderId,  String sku,  Integer quantity) {
}
