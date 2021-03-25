package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AbstractResourceRepresentationLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources/{id}/representations")
@Tag(name = "Resources", description = "Endpoints for linking representations to resources")
public class ResourceRepresentations extends BaseResourceChildController<AbstractResourceRepresentationLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
}
