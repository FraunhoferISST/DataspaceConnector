package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyRestrictionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.Data;
import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.RemoteData;
import de.fraunhofer.isst.dataspaceconnector.repositories.ArtifactRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.ArtifactRetriever;
import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.VerificationResult;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the basic logic for artifacts.
 */
@Log4j2
@Service
public class ArtifactService extends BaseEntityService<Artifact, ArtifactDesc>
        implements RemoteResolver {

    /**
     * Repository for storing data.
     **/
    private final @NonNull DataRepository dataRepository;

    /**
     * Service for http communication.
     **/
    private final @NonNull HttpService httpService;

    /**
     * Constructor for ArtifactService.
     * @param dataRepository The data repository.
     * @param httpService    The HTTP service for fetching remote data.
     */
    @Autowired
    public ArtifactService(final DataRepository dataRepository, final HttpService httpService) {
        this.dataRepository = dataRepository;
        this.httpService = httpService;
    }

    /**
     * Persist the artifact and its data.
     * @param artifact The artifact to persists.
     * @return The persisted artifact.
     */
    @Override
    protected Artifact persist(final Artifact artifact) {
        final var tmp = (ArtifactImpl) artifact;
        if (tmp.getData() != null) {
            if (tmp.getData().getId() == null) {
                // The data element is new, insert
                dataRepository.saveAndFlush(tmp.getData());
            } else {
                // The data element exists already, check if an update is
                // required
                final var storedCopy = dataRepository.getOne(tmp.getData().getId());
                if (!storedCopy.equals(tmp.getData())) {
                    dataRepository.saveAndFlush(tmp.getData());
                }
            }
        }

        return super.persist(tmp);
    }

    /**
     * Get the artifacts data.
     * @param artifactId The id of the artifact.
     * @param queryInput The query for the backend.
     * @return The artifacts data.
     */
    @Transactional
    public Object getData(final PolicyVerifier<URI> accessVerifier, final ArtifactRetriever retriever, final UUID artifactId, final QueryInput queryInput) {
        final var agreements = ((ArtifactRepository) getRepository())
                .findRequestedResourceAgreementRemoteIds(
                        artifactId);
        for (final var agRemoteId : agreements) {
            if(agRemoteId.equals(AgreementFactory.DEFAULT_REMOTE_ID))
                continue;
            try {
                return getData(accessVerifier, retriever, artifactId,
                               new RetrievalInformation(agRemoteId, queryInput));
            } catch(PolicyRestrictionException ignore) {

            }
        }

        // The artifact is not assigned to any requested resources. It must be offered if it exists.
        return getDataFromInternalDB((ArtifactImpl) get(artifactId), queryInput);
    }

    private Object getDataFromInternalDB(final ArtifactImpl artifact, final QueryInput queryInput) {
        final var data = artifact.getData();

        Object rawData;
        if (data instanceof LocalData) {
            rawData = getData((LocalData) data);
        } else if (data instanceof RemoteData) {
            rawData = getData((RemoteData) data, queryInput);
        } else {
            throw new UnreachableLineException("Unknown data type.");
        }

        artifact.incrementAccessCounter();
        persist(artifact);

        return rawData;
    }

    @Transactional
    public Object getData(final PolicyVerifier<URI> accessVerifier,
                          final ArtifactRetriever retriever, final UUID artifactId, final RetrievalInformation information) throws
            PolicyRestrictionException {
        final var artifact = get(artifactId);

        if(accessVerifier.verify(artifact.getRemoteId()) == VerificationResult.DENIED) {
            log.info("Access denied.");
            throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
        }

        final var shouldDownload = shouldDownload(artifact, information.forceDownload);
        if (shouldDownload) {
            /*
                NOTE: Make this not blocking.
             */
            // TODO add query to retriever
            final var dataStream = retriever.retrieve(artifactId, artifact.getRemoteAddress(),
                                                      information.transferContract);
            try {
                final var blob = new String(dataStream.readAllBytes(), StandardCharsets.UTF_16);
                setData(artifactId, blob);
                artifact.incrementAccessCounter();
                persist(artifact);
                return blob;
            } catch (IOException exception) {
                // TODO Failed to set data.
                // Do not proceed with getData, getData increments the access counter
                log.info("Failed to pull data.");
                return null;
            }
        }

        return getDataFromInternalDB((ArtifactImpl) artifact, null);
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class RetrievalInformation {
        @NonNull URI transferContract;
        Boolean forceDownload;
        final QueryInput queryInput;
    }

    private boolean shouldDownload(final Artifact artifact, final Boolean forceDownload) {
        if (forceDownload == null) {
            /*
                NOTE: Add checks if the data is still up to date. This will remove unnecessary
                downloads.
             */
            return !isDataPresent(((ArtifactImpl) artifact).getData()) || artifact.isAutomatedDownload();
        } else {
            return forceDownload;
        }
    }

    private boolean isDataPresent(final Data data) {
        /*
            NOTE: Check if the data has been downloaded at least once.
         */
        return false;
    }

    /**
     * Get local data.
     * @param data The data container.
     * @return The stored data.
     */
    private Object getData(final LocalData data) {
        // TODO send artifact request if no data is available or the user input says "update".
        // TODO If data belongs to a requested artifact, check contract conditions.
        return data.getValue();
    }

    /**
     * Get remote data.
     * @param data       The data container.
     * @param queryInput The query for the backend.
     * @return The stored data.
     */
    private Object getData(final RemoteData data, final QueryInput queryInput) {
        try {
            if (data.getUsername() != null || data.getPassword() != null) {
                return httpService.sendHttpsGetRequestWithBasicAuth(data.getAccessUrl().toString(),
                                                                    data.getUsername(),
                                                                    data.getPassword(), queryInput);
            } else {
                return httpService.sendHttpsGetRequest(data.getAccessUrl().toString(), queryInput);
            }
        } catch (URISyntaxException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Could not connect to data source. [exception=({})]",
                         exception.getMessage(), exception);
            }
            throw new RuntimeException("Could not connect to data source.", exception);
        }
    }

    /**
     * Finds all artifacts referenced in a specific agreement.
     * @param agreementId ID of the agreement
     * @return list of all artifacts referenced in the agreement
     */
    public List<Artifact> getAllByAgreement(final UUID agreementId) {
        Utils.requireNonNull(agreementId, ErrorMessages.ENTITYID_NULL);
        return ((ArtifactRepository) getRepository()).findAllByAgreement(agreementId);
    }

    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (ArtifactRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }

    @Transactional
    public void setData(final UUID artifactId, final String data) {
        final var localData = ((ArtifactImpl) get(artifactId)).getData();
        if (localData instanceof LocalData) {
            // TODO: Check if the data needs to be sanitized before passing it to JPA.
            // TODO: This probably is some form of duplication with the code in persist
            dataRepository.setLocalData(localData.getId(), data);
        } else {
            throw new RuntimeException("Not implemented");
        }
    }
}
