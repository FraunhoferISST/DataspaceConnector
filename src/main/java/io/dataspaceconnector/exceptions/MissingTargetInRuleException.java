package io.dataspaceconnector.exceptions;

import de.fraunhofer.iais.eis.ContractRequest;
import lombok.Getter;

/**
 * Thrown to indicate that a rule from a contract request does not contain a target.
 */
public class MissingTargetInRuleException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The contract request.
     */
    @Getter
    private ContractRequest contractRequest;

    /**
     * Constructs a MissingTargetInRuleException with the specified contract request and detail
     * message.
     *
     * @param request the contract request.
     * @param msg the detail message.
     */
    public MissingTargetInRuleException(final ContractRequest request, final String msg) {
        super(msg);
        this.contractRequest = request;
    }

    /**
     * Constructs a MissingTargetInRuleException with the specified contract request, detail
     * message and cause.
     *
     * @param request the contract request.
     * @param msg the detail message.
     * @param cause the cause.
     */
    public MissingTargetInRuleException(final ContractRequest request, final String msg,
                                        final Throwable cause) {
        super(msg, cause);
        this.contractRequest = request;
    }

}
