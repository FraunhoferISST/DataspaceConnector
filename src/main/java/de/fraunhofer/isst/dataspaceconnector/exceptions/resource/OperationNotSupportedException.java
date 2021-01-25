package de.fraunhofer.isst.dataspaceconnector.exceptions.resource;

/**
 * Thrown to indicate that this operation on resources is not supported.
 */
public class OperationNotSupportedException extends ResourceException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an OperationNotSupportedException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public OperationNotSupportedException(String msg) {
        super(msg);
    }
}
