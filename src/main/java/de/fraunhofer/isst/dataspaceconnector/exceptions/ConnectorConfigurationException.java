package de.fraunhofer.isst.dataspaceconnector.exceptions;

public class ConnectorConfigurationException extends IllegalArgumentException {
    /**
     * Construct an ConnectorConfigurationException with the specified detail message.
     *
     * @param msg   The detail message.
     */
    public ConnectorConfigurationException(String msg) {
        super(msg);
    }
}
