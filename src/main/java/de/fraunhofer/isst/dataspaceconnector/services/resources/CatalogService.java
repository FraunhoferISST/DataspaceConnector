package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for catalogs.
 */
@Service
@NoArgsConstructor
public class CatalogService extends BaseEntityService<Catalog, CatalogDesc> {
}
