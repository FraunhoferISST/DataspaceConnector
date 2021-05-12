package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URL;

/**
 * Describing connector's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectorDesc extends AbstractDescription<Connector> {

    /**
     * The access url of the connector.
     */
    private URL accessUrl;

    /**
     * The title of the connector.
     */
    private String title;

    /**
     * The registration status of the connector.
     */
    private RegisterStatus registerStatus;

}
