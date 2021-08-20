package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.endpoint.AppEndpointView;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.service.resource.relation.AppEndpointLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between apps and app endpoints.
 */
@RestController
@RequestMapping("/api/apps/{id}/endpoints")
@Tag(name = ResourceName.APPS, description = ResourceDescription.APPS)
public class AppsToEndpointsController extends BaseResourceChildRestrictedController<
        AppEndpointLinker, AppEndpoint, AppEndpointView> {
}
