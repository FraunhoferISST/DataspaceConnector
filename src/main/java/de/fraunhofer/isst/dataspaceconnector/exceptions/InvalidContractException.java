package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that the contract could not be build.
 */
public class InvalidContractException extends ContractException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a InvalidContractException with the specified detail message.
     *
     * @param msg   The detail message.
     */
    public InvalidContractException(final String msg) {
        super(msg);
    }

    /**
     * Construct a InvalidContractException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public InvalidContractException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
