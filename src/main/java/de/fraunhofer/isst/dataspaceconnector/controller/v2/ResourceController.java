package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonUniDirectionalLinkerService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceContractLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceRepresentationLinker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
class ResourceController extends BaseResourceController<Resource, ResourceDesc, ResourceView,
        CommonService<Resource, ResourceDesc, ResourceView>> {
}

@RestController
@RequestMapping("/resources/{id}/representations")
class ResourceRepresentations extends BaseResourceChildController<CommonUniDirectionalLinkerService<ResourceRepresentationLinker>> {
}

@RestController
@RequestMapping("/resources/{id}/contracts")
class ResourceContracts
        extends BaseResourceChildController<CommonUniDirectionalLinkerService<ResourceContractLinker>> {
}
