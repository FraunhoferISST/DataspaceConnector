package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;

public interface InfomodelUpdater<I, O> {
    /**
     * Update an entity that is known to the consumer.
     * @param entity The ids object.
     * @return true If the entity has been updated.
     */
    O update(I entity) throws ResourceNotFoundException;
}
