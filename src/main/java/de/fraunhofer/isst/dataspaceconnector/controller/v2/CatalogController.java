package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.CatalogView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.CatalogService;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ExposesResourceFor(Catalog.class)
@RequestMapping("/api/v2/catalogs")
public class CatalogController extends BaseResourceController<Catalog, CatalogDesc, CatalogView, CatalogService> {
}
