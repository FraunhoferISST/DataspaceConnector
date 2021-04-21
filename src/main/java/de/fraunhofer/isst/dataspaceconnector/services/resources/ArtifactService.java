package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.RemoteData;
import de.fraunhofer.isst.dataspaceconnector.repositories.ArtifactRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles the basic logic for artifacts.
 */
@Log4j2
@Service
public class ArtifactService extends BaseEntityService<Artifact, ArtifactDesc> implements RemoteResolver {

    /**
     * Repository for storing data.
     **/
    private final @NonNull DataRepository dataRepository;

    /**
     * Service for http communication.
     **/
    private final @NonNull HttpService httpService;

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * Constructor for ArtifactService.
     *
     * @param dataRepository The data repository.
     * @param httpService    The HTTP service for fetching remote data.
     * @param messageService Service for processing ids messages.
     */
    @Autowired
    public ArtifactService(final DataRepository dataRepository, final HttpService httpService,
                           final MessageService messageService) {
        this.dataRepository = dataRepository;
        this.httpService = httpService;
        this.messageService = messageService;
    }

    /**
     * Persist the artifact and its data.
     *
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
     *
     * @param artifactId The id of the artifact.
     * @param queryInput The query for the backend.
     * @return The artifacts data.
     */
    @Transactional
    public Object getData(final UUID artifactId, final QueryInput queryInput) {
        final var artifact = get(artifactId);

        // If the user triggers the download manually, an artifact request message is sent. This
        // input has the highest priority.

        // 1. User Input, if true, send ids message, if false not.
        // 2. User input null --> Artifact isAutomatedDownload check. If true bla.... usw.
        // 3

        // The value of the stored artifact is checked. If the boolean is set to true, an artifact
        // request message is sent.
        if (artifact.isAutomatedDownload()) {
            final var remoteAddress = artifact.getRemoteAddress();

//            final var response = messageService.sendArtifactRequestMessage(remoteAddress, artifact, transferContract);
//
//                    if (!messageService.validateArtifactResponseMessage(response)) {
//                        // If the response is not an artifact response message, show the response.
//                        final var content = messageService.getContent(response);
//                        return ControllerUtils.respondWithMessageContent(content);
//                    }
        }

        final var data = ((ArtifactImpl) artifact).getData();

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

    /**
     * Get local data.
     *
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
     *
     * @param data       The data container.
     * @param queryInput The query for the backend.
     * @return The stored data.
     */
    private Object getData(final RemoteData data, final QueryInput queryInput) {
        try {
            if (data.getUsername() != null || data.getPassword() != null) {
                return httpService.sendHttpsGetRequestWithBasicAuth(data.getAccessUrl().toString(),
                        data.getUsername(), data.getPassword(), queryInput);
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
     *
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
}
