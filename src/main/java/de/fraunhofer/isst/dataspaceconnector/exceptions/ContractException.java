package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem regarding the contract occurred.
 */
public class ContractException extends RuntimeException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ContractException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ContractException(final String msg) {
        super(msg);
    }
}
