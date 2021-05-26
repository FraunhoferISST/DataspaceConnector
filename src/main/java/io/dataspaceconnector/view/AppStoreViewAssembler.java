package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.AppControllers.AppStoreController;
import io.dataspaceconnector.controller.configurations.AppControllers.AppStoreToApps;
import io.dataspaceconnector.model.AppStore;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an app store.
 */
@Component
public class AppStoreViewAssembler implements RepresentationModelAssembler<AppStore, AppStoreView>, SelfLinking {

    @Override
    public Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AppStoreController.class);
    }

    @Override
    public AppStoreView toModel(final AppStore appStore) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(appStore, AppStoreView.class);
        view.add(getSelfLink(appStore.getId()));

        final var appLink = WebMvcLinkBuilder
                .linkTo(methodOn(AppStoreToApps.class)
                        .getResource(appStore.getId(), null, null))
                .withRel("apps");
        view.add(appLink);

        return view;
    }
}
