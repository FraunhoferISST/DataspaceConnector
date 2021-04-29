package de.fraunhofer.isst.dataspaceconnector.view;

import org.springframework.hateoas.Link;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Helper for building self-links.
 */
public final class ViewAssemblerHelper {
    /**
     * Default constructor.
     */
    private ViewAssemblerHelper() {
        // Nothing to do here. Intentionally empty.
    }

    /**
     * Build self-link for entity.
     *
     * @param entityId The entity id.
     * @param tClass   The entity class type.
     * @param <T>      Type of the entity.
     * @return The self-link of the entity.
     */
    public static <T> Link getSelfLink(final UUID entityId, final Class<T> tClass) {
        return linkTo(tClass).slash(entityId).withSelfRel();
    }
}
