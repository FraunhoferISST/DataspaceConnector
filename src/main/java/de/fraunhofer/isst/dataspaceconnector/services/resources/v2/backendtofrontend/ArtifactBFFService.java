package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ArtifactView;
import org.springframework.stereotype.Service;

@Service
public final class ArtifactBFFService extends CommonService<Artifact, ArtifactDesc,
        ArtifactView> {
    public Object getData(final EndpointId endpointId) {
        return getResource(endpointId).getData();
    }
}
