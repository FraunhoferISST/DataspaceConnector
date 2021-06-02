package io.dataspaceconnector.exceptions;

import io.dataspaceconnector.model.Agreement;
import lombok.Getter;

public class UnconfirmedAgreementException extends RuntimeException {

    @Getter
    private Agreement agreement;

    public UnconfirmedAgreementException(final Agreement agreement, final String msg) {
        super(msg);
        this.agreement = agreement;
    }

    public UnconfirmedAgreementException(final Agreement agreement, final String msg, final Throwable cause) {
        super(msg, cause);
        this.agreement = agreement;
    }

}
