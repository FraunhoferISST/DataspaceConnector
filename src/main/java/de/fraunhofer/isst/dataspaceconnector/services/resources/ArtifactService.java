package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.RemoteData;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Handles the basic logic for artifacts.
 */
@Service
public class ArtifactService extends BaseEntityService<Artifact, ArtifactDesc> {
    /**
     * Class level logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(ArtifactService.class);

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
     *
     * @param dataRepository the data repository
     * @param httpService the HTTP service for fetching remote data
     */
    @Autowired
    public ArtifactService(final DataRepository dataRepository, final HttpService httpService) {
        this.dataRepository = dataRepository;
        this.httpService = httpService;
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
    public Object getData(final UUID artifactId, final QueryInput queryInput) {
        final var artifact = get(artifactId);
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
     * @param data The data container.
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
        } catch (MalformedURLException exception) {
            LOGGER.warn(
                    "Could not connect to data source. [exception=({})]", exception.getMessage());
            throw new RuntimeException("Could not connect to data source.", exception);
        }
    }
}
