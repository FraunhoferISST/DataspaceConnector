package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

public class OperationNotSupportedException extends ResourceException {

    /**
     * Construct an OperationNotSupportedException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public OperationNotSupportedException(String msg) {
        super(msg);
    }
}
