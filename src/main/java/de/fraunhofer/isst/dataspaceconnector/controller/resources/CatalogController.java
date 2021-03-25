package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.CatalogView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogs")
@Tag(name = "Catalogs", description = "Endpoints for CRUD operations on catalogs")
public class CatalogController extends BaseResourceController<Catalog, CatalogDesc, CatalogView, CatalogService> {
}
