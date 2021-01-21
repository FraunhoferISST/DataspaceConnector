package de.fraunhofer.isst.dataspaceconnector.exceptions.contract;

/**
 * Thrown to indicate that no contract agreement could be found.
 */
public class ContractAgreementNotFoundException extends ContractException {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    /**
     * Construct an UnsupportedPatternException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public ContractAgreementNotFoundException(String msg) {
        super(msg);
    }
}
