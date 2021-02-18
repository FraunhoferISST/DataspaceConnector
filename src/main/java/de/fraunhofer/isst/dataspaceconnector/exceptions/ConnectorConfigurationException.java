package de.fraunhofer.isst.dataspaceconnector.exceptions;

/**
 * Thrown to indicate that a problem with the connector configuration exists.
 */
public class ConnectorConfigurationException extends IllegalArgumentException {
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a ConnectorConfigurationException with the specified detail message.
     *
     * @param msg The detail message.
     */
    public ConnectorConfigurationException(final String msg) {
        super(msg);
    }

    /**
     * Construct a ConnectorConfigurationException with the specified detail message and cause.
     *
     * @param msg   The detail message.
     * @param cause The cause.
     */
    public ConnectorConfigurationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
