package de.fraunhofer.isst.dataspaceconnector.view;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.Link;

import java.util.UUID;

public final class ViewAssemblerHelper {
    /**
     * Default constructor.
     */
    private ViewAssemblerHelper() {
        // Nothing to do here. Intentionally empty.
    }

    public static <T> Link getSelfLink(final UUID entityId, final Class<T> tClass) {
        return linkTo(tClass).slash(entityId).withSelfRel();
    }
}
