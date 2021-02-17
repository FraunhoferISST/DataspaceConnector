package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.RemoteData;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Handles the basic logic for artifacts.
 */
@Service
public class ArtifactService extends BaseEntityService<Artifact, ArtifactDesc> {
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
    private HttpService httpService;

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
        final var tmp = (ArtifactImpl)artifact;
        if (tmp.getData() != null) {
            if (tmp.getData().getId() == null) {
                // The data element is new, insert
                dataRepository.saveAndFlush(tmp.getData());
            } else {
                // The data element exists already, check if an update is
                // required
                final var storedCopy =
                        dataRepository.getOne(tmp.getData().getId());
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
     * @return The artifacts data.
     */
    public Object getData(final UUID artifactId) {
        final var artifact = get(artifactId);
        final var data = ((ArtifactImpl)artifact).getData();

        Object rawData;
        if (data instanceof LocalData) {
            rawData = getData((LocalData) data);
        } else if (data instanceof RemoteData) {
            rawData = getData((RemoteData) data);
        } else {
            throw new NotImplementedException("Unknown data type.");
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
    private Object getData(final LocalData data) { return data.getValue(); }

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
                return httpService.sendHttpsGetRequestWithBasicAuth(
                        data.getAccessUrl().toString(), data.getUsername(),
                        data.getPassword(), null);
            } else {
                return httpService.sendHttpsGetRequest(data
                        .getAccessUrl()
                        .toString(), null);
            }
        } catch (MalformedURLException exception) {
            // TODO: LOG
            throw new RuntimeException("Could not connect to data source.",
                    exception);
        }
    }
}
