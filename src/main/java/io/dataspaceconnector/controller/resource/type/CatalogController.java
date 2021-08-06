package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogView;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing catalogs.
 */
@RestController
@RequestMapping(BasePath.CATALOGS)
@Tag(name = ResourceName.CATALOGS, description = ResourceDescription.CATALOGS)
public class CatalogController extends BaseResourceController<Catalog, CatalogDesc, CatalogView,
        CatalogService> {
}
