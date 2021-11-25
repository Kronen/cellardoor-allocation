package com.kronen.github.cellardoor.allocation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderLine {

    String orderId;
    String sku;
    Integer quantity;
}
