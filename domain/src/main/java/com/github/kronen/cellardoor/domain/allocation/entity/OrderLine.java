package com.github.kronen.cellardoor.domain.allocation.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderLine {

    String id;

    String orderId;

    String sku;

    Integer quantity;
}
