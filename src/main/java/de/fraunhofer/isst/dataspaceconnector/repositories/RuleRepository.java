package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "contractrules", path="contractrules")
public interface RuleRepository extends BaseEntityRepository<ContractRule> {
}
