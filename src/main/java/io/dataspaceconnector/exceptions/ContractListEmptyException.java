package io.dataspaceconnector.exceptions;

import de.fraunhofer.iais.eis.ContractRequest;
import lombok.Getter;

public class ContractListEmptyException extends RuntimeException {

    @Getter
    private ContractRequest contractRequest;

    public ContractListEmptyException(final ContractRequest contractRequest, final String msg) {
        super(msg);
        this.contractRequest = contractRequest;
    }

    public ContractListEmptyException(final ContractRequest contractRequest, final String msg, final Throwable cause) {
        super(msg, cause);
        this.contractRequest = contractRequest;
    }

}
