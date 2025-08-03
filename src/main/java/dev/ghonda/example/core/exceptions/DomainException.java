package dev.ghonda.example.core.exceptions;

import java.io.Serial;

public abstract class DomainException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -312901520171766122L;

    protected DomainException(final String message) {
        super(message);
    }

    protected DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
