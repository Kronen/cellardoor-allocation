package com.github.kronen.cellardoor.common.exceptions;

import lombok.Getter;

@Getter
public class InvalidSkuException extends CommonRuntimeException {

  public static final String INVALID_SKU = "Invalid sku";

  public static final String INVALID_SKU_DETAIL = "Invalid sku: %s";

  private final String sku;

  public InvalidSkuException(String sku) {
    this(sku, null);
  }

  public InvalidSkuException(String sku, Throwable cause) {
    super(INVALID_SKU, String.format(INVALID_SKU_DETAIL, sku), cause);
    this.sku = sku;
  }
}
