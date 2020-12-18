package de.fraunhofer.isst.dataspaceconnector.exceptions.contract;

public class ContractException extends RuntimeException {

    /**
     * Construct a ContractException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ContractException(String msg) {
        super(msg);
    }

    /**
     * Construct a ContractException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ContractException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Construct a ContractException with the specified detail cause.
     *
     * @param cause The cause.
     */
    public ContractException(Throwable cause) {
        super(cause);
    }
}
