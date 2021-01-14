package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class ConnectorConfigurationException extends IllegalArgumentException {

    /**
     * Construct a ConnectorConfigurationException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public ConnectorConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Construct a ConnectorConfigurationException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ConnectorConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
