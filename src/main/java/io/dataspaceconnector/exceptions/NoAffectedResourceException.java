package io.dataspaceconnector.exceptions;

public class NoAffectedResourceException extends RuntimeException {

    public NoAffectedResourceException(final String msg) {
        super(msg);
    }

    public NoAffectedResourceException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
