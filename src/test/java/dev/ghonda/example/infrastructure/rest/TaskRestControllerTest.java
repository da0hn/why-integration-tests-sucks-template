package dev.ghonda.example.infrastructure.rest;

import dev.ghonda.example.configuration.PostgreSQLContainerConfiguration;
import dev.ghonda.example.infrastructure.rest.dto.ApiFailureResponse;
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

    @Test
    @DisplayName("Deve falhar ao criar uma tarefa com externalId duplicado")
    void test2() {
        final var externalId = "duplicated-external-id";

        RestAssured.given()
            .contentType("application/json")
            .body(
                """
                {
                  "externalId": "%s",
                  "title": "Tarefa de Teste Duplicada",
                  "description": "Descrição da tarefa de teste duplicada",
                  "priority": "MEDIUM"
                }
                """.formatted(externalId)
            )
            .when()
            .post("/v1/tasks")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        RestAssured.given()
            .contentType("application/json")
            .body(
                """
                {
                  "externalId": "%s",
                  "title": "Tarefa de Teste Duplicada",
                  "description": "Descrição da tarefa de teste duplicada",
                  "priority": "MEDIUM"
                }
                """.formatted(externalId)
            )
            .when()
            .post("/v1/tasks")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", Matchers.equalTo("Task with externalId %s already exists".formatted(externalId)))
            .body("statusCode", Matchers.equalTo(HttpStatus.BAD_REQUEST.value()))
            .body("timestamp", Matchers.notNullValue())
            .body("path", Matchers.equalTo("/v1/tasks"))
            .body("method", Matchers.equalTo("POST"))
            .body("type", Matchers.equalTo(ApiFailureResponse.Type.BUSINESS.name()));


    }

}
