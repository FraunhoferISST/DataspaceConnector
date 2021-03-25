package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AbstractCatalogResourceLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogs/{id}/resources")
@Tag(name = "Catalogs", description = "Endpoints for linking offered resources to catalogs")
public class CatalogOfferedResources extends BaseResourceChildController<AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
}
