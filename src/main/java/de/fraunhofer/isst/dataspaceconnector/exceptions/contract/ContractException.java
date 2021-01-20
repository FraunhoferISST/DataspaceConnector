package de.fraunhofer.isst.dataspaceconnector.exceptions.contract;

public class ContractException extends RuntimeException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ContractException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ContractException(String msg) {
        super(msg);
    }
}
