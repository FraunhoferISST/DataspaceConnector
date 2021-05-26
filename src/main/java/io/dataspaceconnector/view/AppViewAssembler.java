package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.AppControllers.AppController;
import io.dataspaceconnector.model.App;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * Assembles the REST resource for an app.
 */
@Component
public class AppViewAssembler implements RepresentationModelAssembler<App, AppView>, SelfLinking {

    @Override
    public Link getSelfLink(UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AppController.class);
    }

    @Override
    public AppView toModel(final App app) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(app, AppView.class);
        view.add(getSelfLink(app.getId()));

        return view;
    }
}
