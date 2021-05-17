package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describing generic endpoints properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenericEndpointDesc extends EndpointDesc<GenericEndpoint> {

    /**
     * The absolute path of the generic endpoint.
     */
    private String absolutePath;

}
