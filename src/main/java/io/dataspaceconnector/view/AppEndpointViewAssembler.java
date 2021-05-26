package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.AppControllers.AppEndpointController;
import io.dataspaceconnector.model.AppEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an app endpoint.
 */
@Component
public class AppEndpointViewAssembler implements
        RepresentationModelAssembler<AppEndpoint, AppEndpointView>, SelfLinking {

    @Override
    public Link getSelfLink(UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AppEndpointController.class);
    }

    @Override
    public AppEndpointView toModel(AppEndpoint appEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(appEndpoint, AppEndpointView.class);
        view.add(getSelfLink(appEndpoint.getId()));

        return view;
    }
}
