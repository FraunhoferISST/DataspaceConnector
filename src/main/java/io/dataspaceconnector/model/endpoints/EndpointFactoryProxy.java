package io.dataspaceconnector.model.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndpointFactoryProxy
        extends EndpointFactory<Endpoint, EndpointDesc> {

    @Autowired
    private AppEndpointFactory apps;

    @Autowired
    private ConnectorEndpointFactory connector;

    @Autowired
    private GenericEndpointFactory generic;

    @Override
    protected Endpoint initializeEntity(final EndpointDesc desc) {
        switch (desc.getType()) {
            case APP:
                return apps.initializeEntity((AppEndpointDesc) desc);
            case CONNECTOR:
                return connector.initializeEntity((ConnectorEndpointDesc) desc);
        }

        return generic.initializeEntity((GenericEndpointDesc) desc);
    }

    @Override
    protected boolean updateInternal(final Endpoint endpoint, final EndpointDesc desc) {
        switch (desc.getType()) {
            case APP:
                return apps.updateInternal((AppEndpoint) endpoint, (AppEndpointDesc) desc);
            case CONNECTOR:
                return connector.updateInternal((ConnectorEndpoint) endpoint, (ConnectorEndpointDesc) desc);
        }

        return generic.updateInternal((GenericEndpoint) endpoint, (GenericEndpointDesc) desc);
    }
}
