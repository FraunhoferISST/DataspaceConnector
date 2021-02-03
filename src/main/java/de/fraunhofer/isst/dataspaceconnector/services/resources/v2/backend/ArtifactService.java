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

@Service
public class ArtifactService extends BaseService<Artifact, ArtifactDesc> {
    // TODO Clean up the code / Refactor

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private HttpUtils httpUtils;

    @Override
    Artifact persist(final Artifact artifact) {
        if (artifact.getData() != null) {
            if (artifact.getData().getId() == null) {
                // The data element is new, insert
                dataRepository.saveAndFlush(artifact.getData());
            } else {
                // The data element exists already, check if an update is
                // required
                var storedCopy =
                        dataRepository.getOne(artifact.getData().getId());
                if (!storedCopy.equals(artifact.getData())) {
                    dataRepository.saveAndFlush(artifact.getData());
                }
            }
        }

        return super.persist(artifact);
    }

    public Object getData(final UUID artifactId) {
        var artifact = get(artifactId);
        var data = artifact.getData();

        if (data instanceof LocalData) {
            return getData((LocalData) data);
        }

        if (data instanceof RemoteData) {
            return getData((RemoteData) data);
        }

        throw new NotImplementedException("Unknown data type.");
    }

    private Object getData(final LocalData data) {
        return data.getValue();
    }

    private Object getData(final RemoteData data) {
        //TODO Passthrough Uri not string
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
            // LOG
            throw new RuntimeException("Could not connect to data source.");
        }
    }
}
