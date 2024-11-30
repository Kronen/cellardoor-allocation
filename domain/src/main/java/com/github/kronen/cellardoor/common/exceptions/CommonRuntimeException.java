package com.github.kronen.cellardoor.common.exceptions;

import lombok.Getter;

@Getter
public class CommonRuntimeException extends RuntimeException {

  private final String detail;

  public CommonRuntimeException(String message, String detail) {
    super(message);
    this.detail = detail;
  }

  public CommonRuntimeException(String message, String detail, Throwable cause) {
    super(message, cause);
    this.detail = detail;
  }
}
