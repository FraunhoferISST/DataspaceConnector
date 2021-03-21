package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The repository containing all objects of type {@link
 * de.fraunhofer.isst.dataspaceconnector.model.Agreement}.
 */
@RepositoryRestResource(collectionResourceRel = "agreements", path = "agreements")
public interface AgreementRepository extends BaseEntityRepository<Agreement> {
}
