package io.dataspaceconnector.exceptions;

import de.fraunhofer.iais.eis.ContractRequest;
import lombok.Getter;

public class MissingTargetInRuleException extends RuntimeException {

    @Getter
    private ContractRequest contractRequest;

    public MissingTargetInRuleException(final ContractRequest contractRequest, final String msg) {
        super(msg);
        this.contractRequest = contractRequest;
    }

    public MissingTargetInRuleException(final ContractRequest contractRequest, final String msg, final Throwable cause) {
        super(msg, cause);
        this.contractRequest = contractRequest;
    }

}
