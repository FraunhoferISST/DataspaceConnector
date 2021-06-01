package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.ConfigmanagerController;
import io.dataspaceconnector.model.IdsEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an ids endpoint.
 */
@Component
public class IdsEndpointViewAssembler implements
        RepresentationModelAssembler<IdsEndpoint, IdsEndpointView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                ConfigmanagerController.IdsEndpointController.class);
    }

    @Override
    public final IdsEndpointView toModel(final IdsEndpoint idsEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(idsEndpoint,
                IdsEndpointView.class);
        view.add(getSelfLink(idsEndpoint.getId()));

        return view;
    }
}
