package dev.ghonda.example.infrastructure.rest;

import dev.ghonda.example.configuration.CleanupAfterTest;
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

@DisplayName("Testes de integração do endpoint /v1/tasks")
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
    @CleanupAfterTest
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
            .body("createdAt", Matchers.notNullValue())
            .body("updatedAt", Matchers.notNullValue())
            .body("externalId", Matchers.equalTo("78a5431c-4e7e-4d5d-9917-6c9e01ba8625"))
            .body("title", Matchers.equalTo("Tarefa de Teste"))
            .body("description", Matchers.equalTo("Descrição da tarefa de teste"))
            .body("status", Matchers.equalTo("TODO"))
            .body("priority", Matchers.equalTo("HIGH"));
    }

    @Test
    @CleanupAfterTest
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

    @Test
    @CleanupAfterTest
    @DisplayName("Deve buscar uma tarefa por externalId")
    void test3() {
        final var externalId = "78a5431c-4e7e-4d5d-9917-6c9e01ba8625";

        RestAssured.given()
            .contentType("application/json")
            .body(
                """
                {
                  "externalId": "%s",
                  "title": "Tarefa de Teste",
                  "description": "Descrição da tarefa de teste",
                  "priority": "HIGH"
                }
                """.formatted(externalId)
            )
            .when()
            .post("/v1/tasks")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        RestAssured.given()
            .contentType("application/json")
            .pathParam("externalId", externalId)
            .when()
            .get("/v1/tasks/{externalId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("createdAt", Matchers.notNullValue())
            .body("updatedAt", Matchers.notNullValue())
            .body("externalId", Matchers.equalTo(externalId))
            .body("title", Matchers.equalTo("Tarefa de Teste"))
            .body("description", Matchers.equalTo("Descrição da tarefa de teste"))
            .body("status", Matchers.equalTo("TODO"))
            .body("priority", Matchers.equalTo("HIGH"));
    }

    @Test
    @CleanupAfterTest
    @DisplayName("Deve falhar ao buscar uma tarefa por externalId inexistente")
    void test4() {
        final var nonExistentExternalId = "non-existent-external-id";

        RestAssured.given()
            .contentType("application/json")
            .pathParam("externalId", nonExistentExternalId)
            .when()
            .get("/v1/tasks/{externalId}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", Matchers.equalTo("Task with externalId %s not found".formatted(nonExistentExternalId)))
            .body("statusCode", Matchers.equalTo(HttpStatus.NOT_FOUND.value()))
            .body("timestamp", Matchers.notNullValue())
            .body("path", Matchers.equalTo("/v1/tasks/%s".formatted(nonExistentExternalId)))
            .body("method", Matchers.equalTo("GET"))
            .body("type", Matchers.equalTo(ApiFailureResponse.Type.RESOURCE.name()));
    }

    @Test
    @CleanupAfterTest
    @DisplayName("Deve buscar todas as tarefas")
    void test5() {
        final var externalId1 = "78a5431c-4e7e-4d5d-9917-6c9e01ba8625";
        final var externalId2 = "b2c3d4e5-f6g7-h8i9-j0k1-l2m3n4o5p6q7";

        RestAssured.given()
            .contentType("application/json")
            .body(
                """
                {
                  "externalId": "%s",
                  "title": "Tarefa 1",
                  "description": "Descrição da tarefa 1",
                  "priority": "LOW"
                }
                """.formatted(externalId1)
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
                  "title": "Tarefa 2",
                  "description": "Descrição da tarefa 2",
                  "priority": "MEDIUM"
                }
                """.formatted(externalId2)
            )
            .when()
            .post("/v1/tasks")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        RestAssured.given()
            .contentType("application/json")
            .when()
            .get("/v1/tasks")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", Matchers.equalTo(true))
            .body("data", Matchers.hasSize(2))
            .body("data[0].externalId", Matchers.equalTo(externalId1))
            .body("data[0].title", Matchers.equalTo("Tarefa 1"))
            .body("data[0].description", Matchers.equalTo("Descrição da tarefa 1"))
            .body("data[0].priority", Matchers.equalTo("LOW"))
            .body("data[0].status", Matchers.equalTo("TODO"))
            .body("data[0].createdAt", Matchers.notNullValue())
            .body("data[1].externalId", Matchers.equalTo(externalId2))
            .body("data[1].title", Matchers.equalTo("Tarefa 2"))
            .body("data[1].description", Matchers.equalTo("Descrição da tarefa 2"))
            .body("data[1].priority", Matchers.equalTo("MEDIUM"))
            .body("data[1].status", Matchers.equalTo("TODO"))
            .body("data[1].createdAt", Matchers.notNullValue());
    }

    @Test
    @CleanupAfterTest
    @DisplayName("Deve retornar 200 OK quando não houver tarefas")
    void test6() {
        RestAssured.given()
            .contentType("application/json")
            .when()
            .get("/v1/tasks")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("success", Matchers.equalTo(true))
            .body("data", Matchers.emptyIterable());

    }
}
