package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContractManager {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Check if the transfer contract is valid and the conditions are fulfilled.
     *
     * @param agreementId       The id of the contract.
     * @param requestedArtifact The id of the artifact.
     * @throws IllegalArgumentException  if contract agreement deserialization fails.
     * @throws ResourceNotFoundException if agreement could not be found.
     * @throws ContractException         if the contract agreement does not match the requested
     *                                   artifact or is not confirmed.
     */
    public ContractAgreement validateContract(final URI agreementId, final URI requestedArtifact)
            throws IllegalArgumentException, ResourceNotFoundException, ContractException {
        final var agreement = entityResolver.getAgreementByUri(agreementId);
        final var artifacts = dependencyResolver.getArtifactsByAgreement(agreement);

        final var valid = PolicyUtils.isMatchingTransferContract(artifacts, requestedArtifact);
        if (!valid) {
            // If the requested artifact does not match the agreement, send rejection message.
            throw new ContractException("Transfer contract does not match the requested artifact.");
        }

        // Negotiation has to be finished to make the agreement valid.
        if (!agreement.isConfirmed()) {
            throw new ContractException("Contract agreement has not been confirmed. Send contract "
                    + "agreement message to finish the negotiation sequence.");
        }

        return deserializationService.getContractAgreement(agreement.getValue());
    }
}
