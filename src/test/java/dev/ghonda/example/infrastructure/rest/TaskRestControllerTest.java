package dev.ghonda.example.infrastructure.rest;

import dev.ghonda.example.configuration.PostgreSQLContainerConfiguration;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgreSQLContainerConfiguration.class)
class TaskRestControllerTest {

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.serverPort;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Deve criar uma tarefa com sucesso")
    void test1() {
        RestAssured.given()
            .contentType("application/json")
            .body(
                """
                {
                  "externalId": "78a5431c-4e7e-4d5d-9917-6c9e01ba8625",
                  "title": "Tarefa de Teste",
                  "description": "Descrição da tarefa de teste",
                  "priority": "HIGH"
                }
                """
            )
            .when()
            .post("/v1/tasks")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", Matchers.notNullValue())
            .body("createdAt", Matchers.notNullValue())
            .body("updatedAt", Matchers.notNullValue())
            .body("externalId", Matchers.equalTo("78a5431c-4e7e-4d5d-9917-6c9e01ba8625"))
            .body("title", Matchers.equalTo("Tarefa de Teste"))
            .body("description", Matchers.equalTo("Descrição da tarefa de teste"))
            .body("status", Matchers.equalTo("TODO"))
            .body("priority", Matchers.equalTo("HIGH"));

    }

}
