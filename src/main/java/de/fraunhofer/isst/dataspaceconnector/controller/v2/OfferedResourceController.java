package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ExposesResourceFor(OfferedResource.class)
@RequestMapping("/api/v2/offeredresources")
public class OfferedResourceController extends BaseResourceController<OfferedResource, OfferedResourceDesc, OfferedResourceView, ResourceService<OfferedResource, OfferedResourceDesc>> {
}
