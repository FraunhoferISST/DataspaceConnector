package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationArtifactLinker;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/representations/{id}/artifacts")
@Tag(name = "Representations", description = "Endpoints for linking artifacts to representations")
public class RepresentationArtifactController
        extends BaseResourceChildController<RepresentationArtifactLinker, Artifact, ArtifactView> { }
