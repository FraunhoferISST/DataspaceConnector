package de.fraunhofer.isst.dataspaceconnector.exceptions.contract;

public class UnsupportedPatternException extends ContractException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an UnsupportedPatternException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public UnsupportedPatternException(String msg) {
        super(msg);
    }
}
