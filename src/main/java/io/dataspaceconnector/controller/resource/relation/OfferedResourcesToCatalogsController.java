package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogView;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.service.resource.relation.OfferedResourceCatalogLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between offered resources and catalogs.
 */
@RestController
@RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.CATALOGS)
@Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
public class OfferedResourcesToCatalogsController extends BaseResourceChildController<
        OfferedResourceCatalogLinker, Catalog, CatalogView> {
}
