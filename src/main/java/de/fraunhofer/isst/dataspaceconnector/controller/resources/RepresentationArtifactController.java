package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationArtifactLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/representations/{id}/artifacts")
@Tag(name = "Representations", description = "Endpoints for linking artifacts to representations")
public class RepresentationArtifactController extends BaseResourceChildController<RepresentationArtifactLinker, Representation, RepresentationView> {
}
