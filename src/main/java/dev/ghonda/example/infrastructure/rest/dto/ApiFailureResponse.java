package dev.ghonda.example.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ApiFailureResponse {

    String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime timestamp;

    int statusCode;

    HttpStatusCode status;

    Type type;

    String path;

    String method;

    List<Validation> validations;

    public static ApiFailureResponse of(
        final String message,
        final String method,
        final String uri,
        final HttpStatusCode httpStatus,
        final Type type
    ) {
        return ApiFailureResponse.builder()
            .message(message)
            .timestamp(LocalDateTime.now())
            .statusCode(httpStatus.value())
            .status(httpStatus)
            .type(type)
            .path(uri)
            .method(method)
            .validations(List.of())
            .build();
    }

    public static ApiFailureResponse of(
        final String message,
        final String method,
        final String uri,
        final HttpStatusCode httpStatus,
        final Type type,
        final List<Validation> validations
    ) {
        return ApiFailureResponse.builder()
            .message(message)
            .timestamp(LocalDateTime.now())
            .statusCode(httpStatus.value())
            .status(httpStatus)
            .type(type)
            .path(uri)
            .method(method)
            .validations(validations)
            .build();
    }

    public static ApiFailureResponse of(
        final String message,
        final String method,
        final String uri,
        final HttpStatusCode httpStatus,
        final Type type,
        final Validation validation
    ) {
        return ApiFailureResponse.builder()
            .message(message)
            .timestamp(LocalDateTime.now())
            .statusCode(httpStatus.value())
            .status(httpStatus)
            .type(type)
            .path(uri)
            .method(method)
            .validations(List.of(validation))
            .build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public enum Type {
        VALIDATION,
        BUSINESS,
        INTEGRATION,
        AUTH,
        RESOURCE,
        INTERNAL
    }

    @Value
    @Builder
    public static class Validation {

        String field;

        String rejectedValue;

        String message;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }

    }

}
