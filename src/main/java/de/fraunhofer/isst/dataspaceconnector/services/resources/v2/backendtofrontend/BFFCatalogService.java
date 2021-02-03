package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.CatalogView;
import org.springframework.stereotype.Service;

@Service
class BFFCatalogService extends CommonService<Catalog, CatalogDesc,
        CatalogView> {
}
