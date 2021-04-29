package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describes a catalog's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogDesc extends AbstractDescription<Catalog> {
    /**
     * The title of the catalog.
     */
    private String title;

    /**
     * The description of the catalog.
     */
    private String description;
}
