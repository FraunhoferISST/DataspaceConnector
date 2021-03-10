package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the message could not be build.
 */
public class RequestMessageBuilderException extends MessageException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a RequestMessageBuilderException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public RequestMessageBuilderException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
