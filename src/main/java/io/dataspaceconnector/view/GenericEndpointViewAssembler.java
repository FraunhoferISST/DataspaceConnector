package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.ConfigmanagerController;
import io.dataspaceconnector.model.GenericEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for a generic endpoint.
 */
@Component
public class GenericEndpointViewAssembler
        implements RepresentationModelAssembler<GenericEndpoint, GenericEndpointView>, SelfLinking {

    @Override
    public Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, ConfigmanagerController.GenericEndpointController.class);
    }

    @Override
    public GenericEndpointView toModel(final GenericEndpoint genericEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(genericEndpoint, GenericEndpointView.class);
        view.add(getSelfLink(genericEndpoint.getId()));

        return view;
    }
}
