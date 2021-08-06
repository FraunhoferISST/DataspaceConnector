package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.route.RouteView;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.relation.RouteStepLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing steps.
 */
@RestController
@RequestMapping(BasePath.ROUTES + "/{id}/steps")
@Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
public class RoutesToStepsController extends BaseResourceChildController<
        RouteStepLinker, Route, RouteView> {
}
