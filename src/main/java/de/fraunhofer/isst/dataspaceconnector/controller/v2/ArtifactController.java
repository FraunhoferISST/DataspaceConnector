package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.ArtifactBFFService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.EndpointUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/artifacts")
public final class ArtifactController extends BaseResourceController<Artifact,
        ArtifactDesc, ArtifactView, CommonService<Artifact, ArtifactDesc, ArtifactView>> {
    @RequestMapping(value = "{id}/data", method = RequestMethod.GET)
    public ResponseEntity<Object> getData(@Valid @PathVariable final UUID id) {
        final var currentEndpointId = EndpointUtils.getCurrentEndpoint(id);
        final var artifactService = ((ArtifactBFFService) this.getService());
        return ResponseEntity.ok(artifactService.getData(currentEndpointId));
    }
}
