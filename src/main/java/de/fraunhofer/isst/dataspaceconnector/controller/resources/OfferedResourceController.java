package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
@Tag(name = "Resources", description = "Endpoints for CRUD operations on base resources")
public class OfferedResourceController extends BaseResourceController<OfferedResource, OfferedResourceDesc, OfferedResourceView, ResourceService<OfferedResource, OfferedResourceDesc>> {
}
