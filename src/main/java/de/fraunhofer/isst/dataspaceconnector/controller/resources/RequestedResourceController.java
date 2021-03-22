package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RequestedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requested")
@Tag(name = "Resources", description = "Endpoints for CRUD operations on base resources")
public class RequestedResourceController extends BaseResourceController<RequestedResource, RequestedResourceDesc, RequestedResourceView, ResourceService<RequestedResource, RequestedResourceDesc>> {
}
