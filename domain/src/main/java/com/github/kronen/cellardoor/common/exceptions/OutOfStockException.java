package com.github.kronen.cellardoor.common.exceptions;

import lombok.Getter;

@Getter
public class OutOfStockException extends CommonRuntimeException {

  public static final String BATCH_UNAVAILABLE = "Could not find an available batch to allocate the sku";

  public static final String BATCH_UNAVAILABLE_DETAIL =
      "Could not find an available batch to allocate the sku: %s for order %s with quantity %d";

  private final String sku;
  private final String orderId;
  private final int quantity;

  public OutOfStockException(String sku, String orderId, int quantity) {
    this(sku, orderId, quantity, null);
  }

  public OutOfStockException(String sku, String orderId, int quantity, Throwable cause) {
    super(BATCH_UNAVAILABLE, String.format(BATCH_UNAVAILABLE_DETAIL, sku, orderId, quantity), cause);
    this.sku = sku;
    this.orderId = orderId;
    this.quantity = quantity;
  }
}
