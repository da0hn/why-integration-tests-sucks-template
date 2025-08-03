package dev.ghonda.example.core.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class DomainFieldValidationException extends DomainValidationException {

    @Serial
    private static final long serialVersionUID = 8338048800266572819L;

    private final String field;

    private final String rejectedValue;

    private DomainFieldValidationException(
        final String message,
        final String field,
        final String rejectedValue
    ) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    private DomainFieldValidationException(
        final String message,
        final String field,
        final String rejectedValue,
        final Throwable cause
    ) {
        super(message, cause);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String message;

        private String field;

        private String rejectedValue;

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder field(final String field) {
            this.field = field;
            return this;
        }

        public Builder rejectedValue(final Object rejectedValue) {
            this.rejectedValue = rejectedValue != null ? rejectedValue.toString() : "null" ;
            return this;
        }

        public void throwException() {
            throw new DomainFieldValidationException(
                this.message,
                this.field,
                this.rejectedValue != null ? this.rejectedValue : "null"
            );
        }

    }

}
