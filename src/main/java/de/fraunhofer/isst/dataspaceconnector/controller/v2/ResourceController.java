package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/resources")
public class ResourceController extends BaseResourceController<Resource, ResourceDesc, ResourceView,
        CommonService<Resource, ResourceDesc, ResourceView>> {
}
