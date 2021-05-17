package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class EndpointDesc<T extends Endpoint> extends AbstractDescription<T> {

    /**
     * The documentation for the endpoint.
     */
    private URI endpointDocumentation;

    /**
     * The information for the endpoint.
     */
    private String endpointInformation;

    /**
     * The inbound path.
     */
    private String inboundPath;

    /**
     * The outbound path.
     */
    private String outboundPath;
}
