package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppEndpointDesc extends EndpointDesc<AppEndpoint>{

    private URI accessURL;

    private String mediaType;

    private int appEndpointPort;

    private String appEndpointProtocol;

    private String language;

    private AppEndpointType appEndpointType;
}
