package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Catalog;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Catalog}.
 */
@Repository
public interface CatalogRepository extends BaseEntityRepository<Catalog> {
}
