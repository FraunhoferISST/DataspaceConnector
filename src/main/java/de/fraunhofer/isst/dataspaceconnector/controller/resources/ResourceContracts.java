package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AbstractResourceContractLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources/{id}/contracts")
@Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
public class ResourceContracts
        extends BaseResourceChildController<AbstractResourceContractLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
}
