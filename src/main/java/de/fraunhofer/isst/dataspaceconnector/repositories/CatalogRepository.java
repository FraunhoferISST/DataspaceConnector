package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The repository containing all objects of type {@link Catalog}.
 */
@RepositoryRestResource(collectionResourceRel = "catalogs", path = "catalogs")
public interface CatalogRepository extends BaseEntityRepository<Catalog> {
}
