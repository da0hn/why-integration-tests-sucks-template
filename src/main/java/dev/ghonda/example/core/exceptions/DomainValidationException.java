package dev.ghonda.example.core.exceptions;

import java.io.Serial;

public class DomainValidationException extends DomainException {

    @Serial
    private static final long serialVersionUID = -2557401677432181550L;

    public DomainValidationException(final String message) {
        super(message);
    }

    public DomainValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
