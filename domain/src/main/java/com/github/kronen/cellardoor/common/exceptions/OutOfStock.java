package com.github.kronen.cellardoor.common.exceptions;

public class OutOfStock extends Exception {

    public static final String BATCH_UNAVAILABLE = "Could not find an available batch to allocate the sku.";

    public OutOfStock(String errorMessage) {
        super(errorMessage);
    }
}
