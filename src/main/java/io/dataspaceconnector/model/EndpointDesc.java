package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = true)
public class EndpointDesc<T extends Endpoint> extends AbstractDescription<T> {

    private URI endpointDocumentation;

    private String endpointInformation;

    private String inboundPath;

    private String outboundPath;
}
