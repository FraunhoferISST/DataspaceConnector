package de.fraunhofer.isst.dataspaceconnector.repositories.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;

interface CatalogRepository extends BaseResourceRepository<Catalog> {
}

interface ResourceRepository extends BaseResourceRepository<Resource> {
}

interface RepresentationRepository
        extends BaseResourceRepository<Representation> {
}

interface ArtifactRepository extends BaseResourceRepository<Artifact> {
}

interface ContractRepository extends BaseResourceRepository<Contract> {
}

interface RuleRepository extends BaseResourceRepository<ContractRule> {
}
