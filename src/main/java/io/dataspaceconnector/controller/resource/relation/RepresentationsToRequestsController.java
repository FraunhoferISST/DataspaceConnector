package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.resource.RequestedResourceView;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.service.resource.relation.RepresentationOfferedResourceLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between representations and requested
 * resources.
 */
@RestController
@RequestMapping(BasePath.REPRESENTATIONS + "/{id}/" + BaseType.REQUESTS)
@Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
public class RepresentationsToRequestsController extends BaseResourceChildController<
        RepresentationOfferedResourceLinker, RequestedResource, RequestedResourceView> {
}
