package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.ConfigmanagerController;
import io.dataspaceconnector.model.IdentityProvider;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an identity provider.
 */
@Component
public class IdentityProviderViewAssembler implements
        RepresentationModelAssembler<IdentityProvider, IdentityProviderView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                ConfigmanagerController.IdentityProviderController.class);
    }

    @Override
    public final IdentityProviderView toModel(final IdentityProvider identityProvider) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(identityProvider,
                IdentityProviderView.class);
        view.add(getSelfLink(identityProvider.getId()));

        return view;
    }
}
