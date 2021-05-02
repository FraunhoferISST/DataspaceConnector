package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationServices;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.ContractManager;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityPersistenceService {

    /**
     * Service for contract agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for linking agreements and artifacts.
     */
    private final @NonNull RelationServices.AgreementArtifactLinker linker;

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Save contract agreement to database (consumer side).
     *
     * @param agreement The ids contract agreement.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public UUID saveContractAgreement(final ContractAgreement agreement) throws PersistenceException {
        try {
            final var agreementId = agreement.getId();
            final var rdf = IdsUtils.toRdf(agreement);

            final var desc = new AgreementDesc(agreementId, true, rdf, null);

            // Save agreement to return its id.
            return agreementService.create(desc).getId();
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not store contract agreement. [exception=({})]",
                        e.getMessage(), e);
            }
            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    /**
     * Builds a contract agreement from a contract request and saves this agreement to the database
     * with relation to the targeted artifacts (provider side).
     *
     * @param request    The ids contract request.
     * @param targetList List of artifacts.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public ContractAgreement buildAndSaveContractAgreement(
            final ContractRequest request, final List<URI> targetList) throws PersistenceException {
        UUID agreementUuid = null;
        try {
            // Get base URL of application and path to agreements API.
            final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            final var path = ResourceControllers.AgreementController.class.getAnnotation(
                    RequestMapping.class).value()[0];

            // Persist empty agreement to generate UUID.
            agreementUuid = agreementService.create(new AgreementDesc()).getId();

            // Construct ID of contract agreement (URI) using base URL, path and the UUID.
            final var agreementId = URI.create(baseUrl + path + "/" + agreementUuid);

            // Build the contract agreement using the constructed ID
            final var agreement = contractManager.buildContractAgreement(request,
                    agreementId);

            // Iterate over all targets to get the UUIDs of the corresponding artifacts.
            final var artifactList = new ArrayList<UUID>();
            for (final var target : targetList) {
                final var uuid = EndpointUtils.getUUIDFromPath(target);
                artifactList.add(uuid);
            }

            final var rdf = IdsUtils.toRdf(agreement);

            final var desc = new AgreementDesc();
            desc.setConfirmed(false);
            desc.setValue(rdf);

            // Update agreement in database using its previously set id.
            agreementService.update(EndpointUtils.getUUIDFromPath(agreement.getId()), desc);

            // Add artifacts to agreement using the linker.
            linker.add(EndpointUtils.getUUIDFromPath(agreement.getId()),
                    new HashSet<>(artifactList));

            return agreement;
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not store contract agreement. [exception=({})]",
                        e.getMessage(), e);
            }

            // if agreement cannot be saved, remove empty agreement from database
            if (agreementUuid != null) {
                agreementService.delete(agreementUuid);
            }

            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }
}
