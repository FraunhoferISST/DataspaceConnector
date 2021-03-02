package de.fraunhofer.isst.dataspaceconnector.repositories;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;

/**
 * The repository containing all objects of type {@link
 * de.fraunhofer.isst.dataspaceconnector.model.Catalog}.
 */
@RepositoryRestResource(collectionResourceRel = "contracts", path = "contracts")
public interface ContractRepository extends BaseEntityRepository<Contract> {
}
