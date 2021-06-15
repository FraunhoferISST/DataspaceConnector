package io.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the transfer contract is missing in an ArtifactRequestMessage.
 */
public class NoTransferContractException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NoTransferContractException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoTransferContractException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a NoTransferContractException with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause.
     */
    public NoTransferContractException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
