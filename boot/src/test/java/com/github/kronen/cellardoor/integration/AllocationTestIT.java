package com.github.kronen.cellardoor.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import com.github.kronen.cellardoor.RandomRefs;
import com.github.kronen.cellardoor.domain.allocation.entity.Batch;
import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.domain.allocation.port.BatchRepository;
import com.github.kronen.cellardoor.dto.AllocateRequestDTO;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AllocationTestIT {

  @LocalServerPort
  int port;

  @Autowired
  BatchRepository batchRepository;

  WebTestClient webClient;

  private static Batch createBatch(String earlyBatch, String sku, int purchasedQuantity, OffsetDateTime eta) {
    return Instancio.of(Batch.class)
        .set(field(Batch::getReference), earlyBatch)
        .set(field(Batch::getSku), sku)
        .set(field(Batch::getPurchasedQuantity), purchasedQuantity)
        .set(field(Batch::getEta), eta)
        .ignore(field(Batch::getAllocations))
        .create();
  }

  @BeforeEach
  public void setUp() {
    webClient =
        WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void shouldReturn200StatusCodeAndExpectedResponseBodyWhenGettingBatchAllocation() {
    var sku = RandomRefs.randomSku();

    addStock(List.of(createBatch("batch-001", sku, 100, null)));

    // spotless:off
    webClient
        .get()
        .uri("/batch/{batch_reference}", "batch-001").exchange()
        .expectStatus().isOk()
        .expectBody()
            .jsonPath("$.reference").isEqualTo("batch-001")
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

    addStock(List.of(
        createBatch(earlyBatch, sku, 100, OffsetDateTime.parse("2024-11-15T00:00:00Z")),
        createBatch(laterBatch, sku, 100, OffsetDateTime.parse("2024-11-16T00:00:00Z")),
        createBatch(otherBatch, otherSku, 100, null)));

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
    postToAddBatch(earlyBatch, sku, 100, OffsetDateTime.parse("2024-11-145T00:00:00Z"));
    postToAddBatch(otherBatch, otherSku, 100, null);

    // Allocate the batch
    WebTestClient.ResponseSpec response = postToAllocate(orderId, sku, 3);
    response.expectStatus().isEqualTo(HttpStatus.ACCEPTED);

    // Get the allocation and assert the response
    // FIXME using OrderLine until Allocation object is implemented
    getAllocation(orderId)
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody(OrderLine.class)
        .isEqualTo(OrderLine.builder().sku(sku).id(earlyBatch).build());
  }

  WebTestClient.ResponseSpec getAllocation(String orderId) {
    return webClient.get().uri("/allocations/" + orderId).exchange();
  }

  void addStock(List<Batch> batch) {
    this.batchRepository.saveAll(Flux.fromIterable(batch)).blockLast();
  }

  void postToAddBatch(String ref, String sku, int qty, OffsetDateTime eta) {
    webClient
        .post()
        .uri("/add_batch")
        .bodyValue(Batch.builder()
            .reference(ref)
            .sku(sku)
            .purchasedQuantity(qty)
            .eta(eta)
            .build())
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
