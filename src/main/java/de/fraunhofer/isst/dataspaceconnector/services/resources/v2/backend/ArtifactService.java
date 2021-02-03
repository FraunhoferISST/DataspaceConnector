package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RemoteData;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.utils.HttpUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Handles the basic logic for artifacts.
 */
@Service
public class ArtifactService extends BaseService<Artifact, ArtifactDesc> {
    // TODO Clean up the code / Refactor

    /**
     * Repository for storing data.
     **/
    @Autowired
    private DataRepository dataRepository;

    /**
     * Service for http communication.
     **/
    @Autowired
    private HttpUtils httpUtils;

    /**
     * Default constructor.
     */
    protected ArtifactService() {
        super();
    }

    /**
     * Persist the artifact and its data.
     *
     * @param artifact The artifact to persists.
     * @return The persisted artifact.
     */
    @Override
    protected Artifact persist(final Artifact artifact) {
        if (artifact.getData() != null) {
            if (artifact.getData().getId() == null) {
                // The data element is new, insert
                dataRepository.saveAndFlush(artifact.getData());
            } else {
                // The data element exists already, check if an update is
                // required
                final var storedCopy =
                        dataRepository.getOne(artifact.getData().getId());
                if (!storedCopy.equals(artifact.getData())) {
                    dataRepository.saveAndFlush(artifact.getData());
                }
            }
        }

        return super.persist(artifact);
    }

    /**
     * Get the artifacts data.
     *
     * @param artifactId The id of the artifact.
     * @return The artifacts data.
     */
    public Object getData(final UUID artifactId) {
        final var artifact = get(artifactId);
        final var data = artifact.getData();

        if (data instanceof LocalData) {
            return getData((LocalData) data);
        }

        if (data instanceof RemoteData) {
            return getData((RemoteData) data);
        }

        throw new NotImplementedException("Unknown data type.");
    }

    /**
     * Get local data.
     *
     * @param data The data container.
     * @return The stored data.
     */
    private Object getData(final LocalData data) {
        return data.getValue();
    }

    /**
     * Get remote data.
     *
     * @param data The data container.
     * @return The stored data.
     */
    private Object getData(final RemoteData data) {
        //TODO: Passthrough Uri not string
        try {
            if (data.getUsername() != null || data.getPassword() != null) {
                return httpUtils.sendHttpsGetRequestWithBasicAuth(
                        data.getAccessUrl().toString(), data.getUsername(),
                        data.getPassword());
            } else {
                return httpUtils.sendHttpsGetRequest(data
                        .getAccessUrl()
                        .toString());
            }
        } catch (MalformedURLException exception) {
            // TODO: LOG
            throw new RuntimeException("Could not connect to data source.",
                    exception);
        }
    }
}
