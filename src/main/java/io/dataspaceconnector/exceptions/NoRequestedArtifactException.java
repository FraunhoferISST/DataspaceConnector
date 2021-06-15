package io.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the requested artifact is missing in an ArtifactRequestMessage.
 */
public class NoRequestedArtifactException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NoRequestedArtifactException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoRequestedArtifactException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a NoRequestedArtifactException with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause.
     */
    public NoRequestedArtifactException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
