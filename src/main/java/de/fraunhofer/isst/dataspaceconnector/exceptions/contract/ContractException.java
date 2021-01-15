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
}
