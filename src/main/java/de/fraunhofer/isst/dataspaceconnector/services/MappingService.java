package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;

public class MappingService {

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     */
    public RequestedResourceDesc fromIdsResource(final Resource resource) {
        // TODO Mapping
        return null;
    }

    /**
     * Map ids representation to connector representation.
     *
     * @param representation The ids representation.
     * @return The connector representation.
     */
    public RepresentationDesc fromIdsRepresentation(final Representation representation) {
        // TODO Mapping
        return null;
    }

    public ArtifactDesc fromIdsArtifact(final Artifact artifact) {
        // TODO Mapping
        return null;
    }

    public ContractDesc fromIdsContract(final Contract contract) {
        // TODO Mapping
        return null;
    }

    public ContractRuleDesc fromIdsRule(final Rule rule) {
        // TODO Mapping
        return null;
    }
}
