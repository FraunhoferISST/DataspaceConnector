package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AbstractResourceRepresentationLinker;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/offers/{id}/representations")
@Tag(name = "Resources", description = "Endpoints for linking representations to resources")
public class ResourceRepresentations
        extends BaseResourceChildController<AbstractResourceRepresentationLinker<OfferedResource>,
                Representation, RepresentationView> { }
