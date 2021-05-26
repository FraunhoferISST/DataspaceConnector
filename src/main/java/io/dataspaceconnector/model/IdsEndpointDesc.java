package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describing ids endpoints properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdsEndpointDesc extends EndpointDesc<IdsEndpoint>{

    /**
     * The absolute path of the generic endpoint.
     */
    private URI accessURL;

}
