package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class GenericEndpointFactory extends EndpointFactory<GenericEndpoint, GenericEndpointDesc> {

    @Override
    public boolean update(GenericEndpoint entity, GenericEndpointDesc desc) {
        return false;
    }

    @Override
    protected GenericEndpoint createInternal(GenericEndpointDesc desc) {
        return null;
    }
}
