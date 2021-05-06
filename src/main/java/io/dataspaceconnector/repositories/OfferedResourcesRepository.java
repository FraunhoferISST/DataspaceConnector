package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.OfferedResource;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link OfferedResource}.
 */
@Repository
public interface OfferedResourcesRepository extends BaseEntityRepository<OfferedResource> {
}
