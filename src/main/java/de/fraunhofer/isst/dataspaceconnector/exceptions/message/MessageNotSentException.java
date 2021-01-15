package de.fraunhofer.isst.dataspaceconnector.exceptions.message;

public class MessageNotSentException extends MessageException {

    /**
     * Construct a MessageNotSentException with the specified detail message and cause.
     *
     * @param msg The detail message.
     */
    public MessageNotSentException(String msg) {
        super(msg);
    }
}
