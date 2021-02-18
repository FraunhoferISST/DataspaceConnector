package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.view.CatalogView;
import org.springframework.stereotype.Service;

@Service
public class BFFCatalogService<T extends Resource> extends CommonService<Catalog, CatalogDesc, CatalogView> {
}

//@Service
//class OfferedResourceCatalogBFFService extends BFFCatalogService<OfferedResource>{}
//
//@Service
//class RequestedResourceCatalogBFFService extends BFFCatalogService<RequestedResource>{}
