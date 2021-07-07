package io.dataspaceconnector.view;

import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class EndpointViewAssemblerProxy
        implements RepresentationModelAssembler<Endpoint, RepresentationModel<?>> {

    /**
     * Assembler for generic endpoints.
     */
    @Autowired
    private GenericEndpointViewAssembler genericAssembler;

    /**
     * Assembler for app endpoints.
     */
    @Autowired
    private AppEndpointViewAssembler appAssembler;

    /**
     * Assembler for connector endpoints.
     */
    @Autowired
    private ConnectorEndpointViewAssembler connectorAssembler;

    @Override
    public RepresentationModel<?> toModel(final Endpoint endpoint) {
        if (AppEndpoint.class.equals(endpoint.getClass())) {
            return appAssembler.toModel((AppEndpoint) endpoint);
        }

        if (ConnectorEndpoint.class.equals(endpoint.getClass())) {
            return connectorAssembler.toModel((ConnectorEndpoint) endpoint);
        }

        return genericAssembler.toModel((GenericEndpoint) endpoint);
    }
}
