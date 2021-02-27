package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationArtifactLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/representations/{id}/artifacts")
@Tag(name = "Representations")
public class RepresentationArtifactController extends BaseResourceChildController<RepresentationArtifactLinker> {
}
