package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityDependencyResolver {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityDependencyResolver.class);

    /**
     * Service for persisting and querying contracts.
     */
    private final @NonNull ContractService contractService;

    /**
     * Gets all contracts applicable for a specific artifact by using the query defined in the
     * {@link de.fraunhofer.isst.dataspaceconnector.repositories.ContractRepository}.
     *
     * @param artifactId The artifact id.
     * @return List of contract offers.
     */
    public List<Contract> getContractOffersByArtifactId(final URI artifactId) {
        final var uuid = EndpointUtils.getUUIDFromPath(artifactId);
        return contractService.getByArtifactId(uuid);
    }
}
