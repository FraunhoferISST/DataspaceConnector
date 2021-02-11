package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.CatalogView;
import org.springframework.stereotype.Service;

@Service
class BFFCatalogService extends CommonService<Catalog, CatalogDesc,
        CatalogView> {
}
