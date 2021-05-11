package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describes an app endpoint's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppEndpointDesc extends EndpointDesc<AppEndpoint>{

    /**
     * The access url of the endpoint.
     */
    private URI accessURL;

    /**
     * The file name extension of the data.
     */
    private String mediaType;

    /**
     * The port number of the app endpoint.
     */
    private int appEndpointPort;

    /**
     * The protocol of the app endpoint.
     */
    private String appEndpointProtocol;

    /**
     * The used language.
     */
    private String language;

    /**
     * The type of the app endpoint.
     */
    private AppEndpointType appEndpointType;
}
