package io.dataspaceconnector.exceptions;

public class NoRequestedArtifactException extends RuntimeException {

    public NoRequestedArtifactException(final String msg) {
        super(msg);
    }

    public NoRequestedArtifactException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
