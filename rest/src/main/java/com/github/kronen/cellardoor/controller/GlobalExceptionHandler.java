package com.github.kronen.cellardoor.controller;

import java.net.URI;

import com.github.kronen.cellardoor.common.exceptions.InvalidSkuException;
import com.github.kronen.cellardoor.common.exceptions.OutOfStockException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OutOfStockException.class)
  public Mono<ProblemDetail> handleOutOfStockException(OutOfStockException ex) {
    log.atError()
        .setMessage(ex.getMessage())
        .addKeyValue("sku", ex.getSku())
        .addKeyValue("orderId", ex.getOrderId())
        .addKeyValue("quantity", ex.getQuantity())
        .setCause(ex.getCause())
        .log();

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getDetail());
    problemDetail.setType(URI.create("/errors/out-of-stock"));
    problemDetail.setTitle("Out of Stock");
    problemDetail.setProperty("sku", ex.getSku());
    problemDetail.setProperty("orderId", ex.getOrderId());
    problemDetail.setProperty("quantity", ex.getQuantity());
    problemDetail.setInstance(URI.create(String.format(
        "/allocate?orderId=%s&sku=%s&quantity=%d", ex.getOrderId(), ex.getSku(), ex.getQuantity())));
    return Mono.just(problemDetail);
  }

  @ExceptionHandler(InvalidSkuException.class)
  public Mono<ProblemDetail> handleInvalidSkuException(InvalidSkuException ex) {
    log.atError()
        .setMessage(ex.getMessage())
        .addKeyValue("sku", ex.getSku())
        .setCause(ex.getCause())
        .log();

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getDetail());
    problemDetail.setType(URI.create("/errors/invalid-sku"));
    problemDetail.setTitle("Invalid sku");
    problemDetail.setProperty("sku", ex.getSku());
    problemDetail.setInstance(URI.create(String.format("/allocate?sku=%s", ex.getSku())));

    return Mono.just(problemDetail);
  }
}
