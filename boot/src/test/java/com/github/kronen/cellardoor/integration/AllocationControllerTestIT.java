package com.github.kronen.cellardoor.integration;

import static com.github.kronen.cellardoor.config.TestConfiguration.MONGO_VERSION;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.instancio.Select.field;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;
import com.github.kronen.cellardoor.infraestructure.batch.repository.MongoBatchRepository;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AllocationControllerTestIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGO_VERSION);

    @LocalServerPort
    int port;

    @Autowired
    MongoBatchRepository batchRepository;

    @Test
    void whenRequestGet_thenOK() {
        BatchDocument batch = Instancio.of(BatchDocument.class)
                .set(field(BatchDocument::getReference), "batch-001")
                .create();
        batchRepository.save(batch).block();

        // @formatter:off
        given().port(port)
                .pathParam("batch_reference", "batch-001")
                .when()
                .get("/allocation/batch/{batch_reference}")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(200)
                .body("reference", equalTo("batch-001"), "sku", equalTo(batch.getSku()));
        // @formatter:on
    }
}
