package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that this line in the code should not have been possible to reach.
 */
public class UnreachableLineException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a UnexpectedMessageType with the specified detail message.
     *
     * @param msg The detail message.
     */
    public UnreachableLineException(final String msg) {
        super(msg);
    }
}
