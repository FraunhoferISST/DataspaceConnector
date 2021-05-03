package de.fraunhofer.isst.dataspaceconnector.services.ids.updater;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;

/**
 * Updates an DSC object by providing an IDS object.
 * @param <I> The IDS object type.
 * @param <O> The DSC object type.
 */
public interface InfomodelUpdater<I, O extends AbstractEntity> {
    /**
     * Update an entity that is known to the consumer.
     * @param entity The ids object.
     * @return The updated dsc object.
     */
    O update(I entity) throws ResourceNotFoundException;
}
