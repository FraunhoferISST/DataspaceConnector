package de.fraunhofer.isst.dataspaceconnector.view;

import org.springframework.hateoas.Link;

import java.util.UUID;

/**
 * Interface for generating the self-link of entities.
 */
public interface SelfLinking {
    /**
     * Construct self-link of an entity.
     *
     * @param entityId The id of the entity.
     * @return The self-link of the entity.
     */
    Link getSelfLink(UUID entityId);
}
