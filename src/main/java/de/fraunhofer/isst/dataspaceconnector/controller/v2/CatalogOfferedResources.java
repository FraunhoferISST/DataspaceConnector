package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.CatalogResourceLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/catalogs/{id}/resources")
@Tag(name = "Catalogs")
public class CatalogOfferedResources extends BaseResourceChildController<CatalogResourceLinker<OfferedResource>> {
}
