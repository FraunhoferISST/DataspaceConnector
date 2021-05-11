package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GenericEndpointDesc extends EndpointDesc<GenericEndpoint> {

    private String absolutePath;

}
