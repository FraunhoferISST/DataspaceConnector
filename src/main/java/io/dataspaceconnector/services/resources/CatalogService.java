package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.CatalogDesc;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for catalogs.
 */
@Service
@NoArgsConstructor
public class CatalogService extends BaseEntityService<Catalog, CatalogDesc> {
}
