package com.github.kronen.cellardoor.domain.allocation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderLine {

    String orderId;
    String sku;
    Integer quantity;
}
