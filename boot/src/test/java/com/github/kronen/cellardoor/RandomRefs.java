package com.github.kronen.cellardoor;

public class RandomRefs {

  public static String randomSuffix() {
    return "sss";
  }

  public static String randomSku() {
    return "sku-" + randomSuffix();
  }

  public static String randomSku(String name) {
    return "sku-" + name + "-" + randomSuffix();
  }

  public static String randomBatchRef(int name) {
    return "batch-" + name + "-" + randomSuffix();
  }

  public static String randomOrderId() {
    return "order-" + randomSuffix();
  }

  public static String randomOrderId(int name) {
    return "order-" + name + "-" + randomSuffix();
  }
}
