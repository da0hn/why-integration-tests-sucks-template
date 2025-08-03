package dev.ghonda.example.configuration;

import dev.ghonda.example.core.exceptions.DomainFieldValidationException;
import dev.ghonda.example.core.exceptions.DomainValidationException;
import dev.ghonda.example.core.exceptions.ResourceNotFoundException;
import dev.ghonda.example.infrastructure.rest.dto.ApiFailureResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiFailureResponse> handleResourceNotFoundException(
        final ResourceNotFoundException exception,
        final HttpServletRequest request
    ) {

        final var status = HttpStatus.NOT_FOUND;
        final var response = ApiFailureResponse.of(
            exception.getMessage(),
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.RESOURCE
        );

        log.error("[GlobalExceptionHandler] Recurso não encontrado: {}", response, exception);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ RuntimeException.class, Exception.class })
    public ResponseEntity<ApiFailureResponse> handleUnexpectedException(
        final RuntimeException exception,
        final HttpServletRequest request
    ) {
        final var status = HttpStatus.INTERNAL_SERVER_ERROR;
        final var response = ApiFailureResponse.of(
            "Ocorreu um erro inesperado",
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.INTERNAL
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro inesperado: {}", response, exception);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<ApiFailureResponse> handleConflict(
        final RuntimeException exception,
        final HttpServletRequest request
    ) {
        final var status = HttpStatus.CONFLICT;
        final var response = ApiFailureResponse.of(
            exception.getMessage(),
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.BUSINESS
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro durante a execução de uma regra de negócio: {}", response, exception);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiFailureResponse> handleConstraintViolationException(
        final ConstraintViolationException exception,
        final HttpServletRequest request
    ) {
        final var status = HttpStatus.UNPROCESSABLE_ENTITY;
        final var validations = exception.getConstraintViolations().stream()
            .map(violation -> {
                final var field = violation.getPropertyPath().toString();

                var rejectedValue = violation.getInvalidValue();

                // Se for a constraint estiver no nivel da classe, tentamos obter o valor do campo específico
                if (violation.getLeafBean() != null && violation.getInvalidValue() == violation.getLeafBean()) {
                    try {
                        rejectedValue = new BeanWrapperImpl(violation.getLeafBean()).getPropertyValue(field);
                    }
                    catch (final Exception e) {
                        log.warn(
                            "[GlobalExceptionHandler] Não foi possível obter o valor do campo '{}' da entidade: {}",
                            field,
                            violation.getLeafBean(),
                            e
                        );
                    }
                }

                return ApiFailureResponse.Validation.builder()
                    .field(field)
                    .rejectedValue(rejectedValue != null ? rejectedValue.toString() : null)
                    .message(violation.getMessage())
                    .build();
            })
            .sorted(Comparator.comparing(ApiFailureResponse.Validation::getField))
            .toList();

        final var response = ApiFailureResponse.of(
            "Falha na validação dos dados",
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.VALIDATION,
            validations
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro na validação dos dados: {}", response);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DomainFieldValidationException.class)
    public ResponseEntity<ApiFailureResponse> handleDomainFieldValidationException(
        final DomainFieldValidationException exception,
        final HttpServletRequest request
    ) {
        final var status = HttpStatus.UNPROCESSABLE_ENTITY;
        final var response = ApiFailureResponse.of(
            exception.getMessage(),
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.VALIDATION,
            ApiFailureResponse.Validation.builder()
                .field(exception.getField())
                .rejectedValue(exception.getRejectedValue())
                .message(exception.getMessage())
                .build()
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro na validação do campo: {}", response, exception);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ApiFailureResponse> handleDomainValidationException(
        final DomainValidationException exception,
        final HttpServletRequest request
    ) {
        final var status = HttpStatus.BAD_REQUEST;
        final var response = ApiFailureResponse.of(
            exception.getMessage(),
            request.getMethod(),
            request.getRequestURI(),
            status,
            ApiFailureResponse.Type.BUSINESS
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro na validação do campo: {}", response, exception);

        return ResponseEntity.status(status).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException exception,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        final var validations = exception.getFieldErrors().stream()
            .map(error -> ApiFailureResponse.Validation.builder()
                .field(error.getField())
                .rejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null)
                .message(error.getDefaultMessage())
                .build())
            .sorted(Comparator.comparing(ApiFailureResponse.Validation::getField))
            .toList();
        final var globalValidations = exception.getGlobalErrors().stream()
            .map(error -> ApiFailureResponse.Validation.builder()
                .field(error.getObjectName())
                .rejectedValue(null)
                .message(error.getDefaultMessage())
                .build())
            .sorted(Comparator.comparing(ApiFailureResponse.Validation::getField))
            .toList();

        final var httpServletRequest = ((ServletWebRequest) request).getRequest();

        final var responseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        final var response = ApiFailureResponse.of(
            "Falha na validação dos dados",
            httpServletRequest.getMethod(),
            httpServletRequest.getRequestURI(),
            responseStatus,
            ApiFailureResponse.Type.VALIDATION,
            Stream.concat(validations.stream(), globalValidations.stream())
                .toList()
        );

        log.error("[GlobalExceptionHandler] Ocorreu um erro na validação dos dados: {}", response);

        return ResponseEntity.status(responseStatus).body(response);
    }

}
