package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonUniDirectionalLinkerService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ResourceContractLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ResourceRepresentationLinker;
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
