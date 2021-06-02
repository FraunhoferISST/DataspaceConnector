package io.dataspaceconnector.exceptions;

public class NoTransferContractException extends RuntimeException {

    public NoTransferContractException(final String msg) {
        super(msg);
    }

    public NoTransferContractException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
