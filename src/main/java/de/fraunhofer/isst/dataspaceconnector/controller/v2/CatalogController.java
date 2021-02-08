package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.CatalogView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.CatalogResourceLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonUniDirectionalLinkerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/catalogs")
class CatalogController extends BaseResourceController<Catalog, CatalogDesc, CatalogView,
        CommonService<Catalog, CatalogDesc, CatalogView>> {
}

@RestController
@RequestMapping("/api/v2/catalogs/{id}/resources")
class CatalogResources extends BaseResourceChildController<CommonUniDirectionalLinkerService<CatalogResourceLinker>> {
}
