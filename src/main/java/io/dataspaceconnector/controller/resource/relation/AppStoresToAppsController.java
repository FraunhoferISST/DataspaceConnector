package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.app.AppView;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.service.resource.relation.AppStoreAppLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between apps and appstores.
 */
@RestController
@RequestMapping("/api/appstores/{id}/apps")
@Tag(name = ResourceName.APPSTORES, description = ResourceDescription.APPSTORES)
public class AppStoresToAppsController extends BaseResourceChildRestrictedController<
        AppStoreAppLinker, App, AppView> {
}
