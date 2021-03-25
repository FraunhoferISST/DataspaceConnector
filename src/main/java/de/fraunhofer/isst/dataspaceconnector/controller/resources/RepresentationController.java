package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/representations")
@Tag(name = "Representations", description = "Endpoints for CRUD operations on representations")
public class RepresentationController
        extends BaseResourceController<Representation, RepresentationDesc, RepresentationView, RepresentationService> {
}
