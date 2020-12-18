package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class UnsupportedPatternException extends ContractException {

    /**
     * Construct an UnsupportedPatternException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public UnsupportedPatternException(String msg) {
        super(msg);
    }

    /**
     * Construct an UnsupportedPatternException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public UnsupportedPatternException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
