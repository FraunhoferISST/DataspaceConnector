package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactView;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.service.resource.relation.RouteArtifactLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoint for managing route artifacts.
 */
@RestController
@RequestMapping(BasePath.ROUTES + "/{id}/outputs")
@Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
public class RoutesToArtifactsController extends BaseResourceChildController<
        RouteArtifactLinker, Artifact, ArtifactView> {
}
