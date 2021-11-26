package com.github.kronen.cellardoor.common.exceptions;

public class OutOfStockException extends Exception {

    public static final String BATCH_UNAVAILABLE = "Could not find an available batch to allocate the sku '%s'";

    public OutOfStockException(String sku) {
        super(String.format(BATCH_UNAVAILABLE, sku));
    }
}
