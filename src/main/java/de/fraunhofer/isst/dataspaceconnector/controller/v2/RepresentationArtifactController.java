package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonUniDirectionalLinkerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/representations/{id}/artifacts")
public class RepresentationArtifactController extends BaseResourceChildController
        <CommonUniDirectionalLinkerService<RepresentationArtifactLinker>> {
}
