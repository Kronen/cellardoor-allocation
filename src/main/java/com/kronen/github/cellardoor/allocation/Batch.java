package com.kronen.github.cellardoor.allocation;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class Batch {

    String reference;
    String sku;
    Integer availableQuantity;
    OffsetDateTime eta;

    public void allocate(OrderLine line) {
        this.availableQuantity -= line.getQuantity();
    }
}
