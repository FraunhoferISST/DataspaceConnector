package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.ConfigmanagerController;
import io.dataspaceconnector.model.Proxy;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an proxy.
 */
@Component
public class ProxyViewAssembler implements
        RepresentationModelAssembler<Proxy, ProxyView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                ConfigmanagerController.ProxyController.class);
    }

    @Override
    public final ProxyView toModel(final Proxy proxy) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(proxy,
                ProxyView.class);
        view.add(getSelfLink(proxy.getId()));

        return view;
    }
}
