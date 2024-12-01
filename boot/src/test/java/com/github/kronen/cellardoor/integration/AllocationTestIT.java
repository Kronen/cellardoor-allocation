package com.github.kronen.cellardoor.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.OffsetDateTime;

import com.github.kronen.cellardoor.RandomRefs;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;
import com.github.kronen.cellardoor.dto.NewBatchDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AllocationTestIT {

  @LocalServerPort
  int port;

  WebTestClient webClient;

  @BeforeEach
  public void setUp() {
    webClient =
        WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void shouldReturn200StatusCodeAndExpectedResponseBodyWhenGettingBatchAllocation() {
    var reference = RandomRefs.randomBatchRef(1);
    var sku = RandomRefs.randomSku();

    postToAddBatch(reference, sku, 100, null);

    // spotless:off
    webClient
        .get()
        .uri("/batch/{batch_reference}", reference).exchange()
        .expectStatus().isOk()
        .expectBody()
            .jsonPath("$.reference").isEqualTo(reference)
            .jsonPath("$.sku").isEqualTo(sku);
    // spotless:on
  }

  @Test
  void shouldReturn201AndAllocatedBatch() {
    var sku = RandomRefs.randomSku();
    var otherSku = RandomRefs.randomSku("other");
    var earlyBatch = RandomRefs.randomBatchRef(1);
    var laterBatch = RandomRefs.randomBatchRef(2);
    var otherBatch = RandomRefs.randomBatchRef(3);

    postToAddBatch(laterBatch, sku, 100, OffsetDateTime.parse("2024-11-15T00:00:00Z"));
    postToAddBatch(earlyBatch, sku, 100, OffsetDateTime.parse("2024-11-14T00:00:00Z"));
    postToAddBatch(otherBatch, otherSku, 100, null);

    // Prepare allocation request
    var orderId = RandomRefs.randomOrderId();

    // Send allocation request
    var response = this.postToAllocate(orderId, sku, 3);

    // spotless:off
    response
        .expectStatus().isEqualTo(HttpStatus.CREATED)
        .expectBody(String.class).isEqualTo(earlyBatch);
    // spotless:on
  }

  @Test
  void shouldReturn400AndErrorMessage() {
    var unknownSku = RandomRefs.randomSku("unknown");
    var orderId = RandomRefs.randomOrderId();

    // Send allocation request
    var response = this.postToAllocate(orderId, unknownSku, 3);

    response.expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST)
        .expectBody(ProblemDetail.class)
        .consumeWith(problemDetail -> {
          var pd = problemDetail.getResponseBody();
          assertThat(pd).isNotNull();
          assertThat(pd.getType()).isEqualTo(URI.create("/errors/invalid-sku"));
          assertThat(pd.getTitle()).isEqualTo("Invalid sku");
          assertThat(pd.getDetail()).isEqualTo("Invalid sku: " + unknownSku);
          assertThat(pd.getInstance()).isEqualTo(URI.create("/allocate?sku=" + unknownSku));
          assertThat(pd.getProperties()).isNotNull().hasFieldOrPropertyWithValue("sku", unknownSku);
        });
  }

  @Disabled
  @Test
  void testHappyPathReturns202AndBatchIsAllocated() {
    var orderId = RandomRefs.randomOrderId();
    var sku = RandomRefs.randomSku();
    var otherSku = RandomRefs.randomSku("other");
    var earlyBatch = RandomRefs.randomBatchRef(1);
    var laterBatch = RandomRefs.randomBatchRef(2);
    var otherBatch = RandomRefs.randomBatchRef(3);

    // Add batches via API
    postToAddBatch(laterBatch, sku, 100, OffsetDateTime.parse("2024-11-15T00:00:00Z"));
    postToAddBatch(earlyBatch, sku, 100, OffsetDateTime.parse("2024-11-14T00:00:00Z"));
    postToAddBatch(otherBatch, otherSku, 100, null);

    // Allocate the batch
    WebTestClient.ResponseSpec response = postToAllocate(orderId, sku, 3);
    response.expectStatus().isEqualTo(HttpStatus.ACCEPTED);

    // Get the allocation and assert the response
    // FIXME using OrderLine until GET Allocation is implemented
    getAllocation(orderId)
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(OrderLine.class)
        .isEqualTo(OrderLine.builder().sku(sku).id(earlyBatch).build());
  }

  WebTestClient.ResponseSpec getAllocation(String orderId) {
    return webClient.get().uri("/allocations/" + orderId).exchange();
  }

  void postToAddBatch(String ref, String sku, int qty, OffsetDateTime eta) {
    webClient
        .post()
        .uri("/batch")
        .bodyValue(new NewBatchDTO()
            .reference(ref)
            .sku(sku)
            .purchasedQuantity(qty)
            .eta(eta))
        .exchange()
        .expectStatus()
        .isCreated();
  }

  WebTestClient.ResponseSpec postToAllocate(String orderid, String sku, int qty) {
    return webClient
        .post()
        .uri("/allocate")
        .bodyValue(new AllocateRequestDTO()
            .orderLine(OrderLine.builder()
                .orderId(orderid)
                .sku(sku)
                .quantity(qty)
                .build()))
        .exchange();
  }
}
