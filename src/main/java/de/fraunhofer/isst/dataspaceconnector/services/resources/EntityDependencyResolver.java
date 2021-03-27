package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityDependencyResolver {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDependencyResolver.class);

    /**
     * Service for linking artifact and representations.
     */
    private final @NonNull RelationshipServices.ArtifactRepresentationLinker artifactLinker;

    /**
     * Service for linking representations and offered resources.
     */
    private final @NonNull RelationshipServices.RepresentationOfferedResourceLinker representationLinker;

    /**
     * Service for linking contracts and offered resources.
     */
    private final @NonNull OfferedResourceContractLinker resourceLinker;

    /**
     * Service for linking contracts and rules.
     */
    private final @NonNull ContractRuleLinker ruleLinker;

    /**
     * Service for handling artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Iterate over all artifacts, representations, and resources to get all contract offers that
     * may match a requested artifact.
     *
     * @param artifactId The artifact id.
     * @return List of contract offers.
     * @throws ResourceNotFoundException If the artifact could not be found.
     */
    public List<Contract> getContractOffersByArtifactId(final URI artifactId) throws ResourceNotFoundException {
        final var uuid = EndpointUtils.getUUIDFromPath(artifactId);
        final var artifact = artifactService.get(uuid);

        final var contractList = new ArrayList<Contract>();
        final var representations = getRepresentationsByArtifact(artifact);
        for (final var representation : representations) {
            final var resources = getOfferedResourcesByRepresentation(representation);

            for (final var resource : resources) {
                final var contracts = getContractsByOfferedResource(resource);
                contractList.addAll(contracts);
            }
        }

        return contractList;
    }

    /**
     * Get representations by artifact.
     *
     * @param artifact The artifact.
     * @return List of representations.
     */
    private List<Representation> getRepresentationsByArtifact(final Artifact artifact) {
        return artifactLinker.getInternal(artifact);
    }

    /**
     * Get resources by representation.
     *
     * @param representation The representations.
     * @return List of offered resources.
     */
    private List<OfferedResource> getOfferedResourcesByRepresentation(final Representation representation) {
        return representationLinker.getInternal(representation);
    }

    /**
     * Get contracts by offered resource.
     *
     * @param resource The offered resource.
     * @return List of contract offers.
     */
    private List<Contract> getContractsByOfferedResource(final OfferedResource resource) {
        return resourceLinker.getInternal(resource);
    }

    /**
     * Get rules by contract offer.
     *
     * @param contract The contract.
     * @return List of rules.
     */
    public List<ContractRule> getRulesByContractOffer(final Contract contract) {
        return ruleLinker.getInternal(contract);
    }
}
