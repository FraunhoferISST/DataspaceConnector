package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class AppEndpointFactory extends EndpointFactory<AppEndpoint, AppEndpointDesc> {

    @Override
    public boolean update(AppEndpoint entity, AppEndpointDesc desc) {
        return false;
    }

    @Override
    protected AppEndpoint createInternal(AppEndpointDesc desc) {
        return null;
    }
}
