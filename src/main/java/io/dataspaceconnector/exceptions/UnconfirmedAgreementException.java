package io.dataspaceconnector.exceptions;

import io.dataspaceconnector.model.Agreement;
import lombok.Getter;

/**
 * Thrown to indicate that an agreement could not be confirmed.
 */
public class UnconfirmedAgreementException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The agreement.
     */
    @Getter
    private final Agreement agreement;

    /**
     * Constructs an UnconfirmedAgreementException with the specified agreement and detail message.
     *
     * @param unconfirmed the agreement.
     * @param msg the detail message.
     */
    public UnconfirmedAgreementException(final Agreement unconfirmed, final String msg) {
        super(msg);
        this.agreement = unconfirmed;
    }

    /**
     * Constructs an UnconfirmedAgreementException with the specified agreement, detail message and
     * cause.
     *
     * @param unconfirmed the agreement.
     * @param msg the detail message.
     * @param cause the cause.
     */
    public UnconfirmedAgreementException(final Agreement unconfirmed, final String msg,
                                         final Throwable cause) {
        super(msg, cause);
        this.agreement = unconfirmed;
    }

}
