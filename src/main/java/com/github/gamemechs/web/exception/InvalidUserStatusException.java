package com.github.gamemechs.web.exception;

/**
 * The {@code UnknownUserStatusException} is thrown when the user status is unknown.
 */
public final class InvalidUserStatusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidUserStatusException() {
        super();
    }

    public InvalidUserStatusException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidUserStatusException(final String message) {
        super(message);
    }
}
