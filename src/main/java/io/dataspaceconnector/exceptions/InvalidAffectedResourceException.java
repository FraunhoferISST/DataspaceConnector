package io.dataspaceconnector.exceptions;

public class InvalidAffectedResourceException extends RuntimeException {

    public InvalidAffectedResourceException(final String msg) {
        super(msg);
    }

    public InvalidAffectedResourceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
