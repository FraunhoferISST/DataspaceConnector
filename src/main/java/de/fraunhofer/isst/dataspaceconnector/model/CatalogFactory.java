package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Creates and updates a catalog.
 */
@Component
public class CatalogFactory implements BaseFactory<Catalog, CatalogDesc> {

    /**
     * Create a new catalog.
     * @param desc The description of the new catalog.
     * @return The new catalog.
     */
    @Override
    public Catalog create(final CatalogDesc desc) {
        final var catalog = new Catalog();
        catalog.setOfferedResources(new HashMap<>());
        catalog.setRequestedResources(new HashMap<>());

        update(catalog, desc);

        return catalog;
    }

    /**
     * Update a catalog.
     *
     * @param catalog The catalog to be updated.
     * @param desc The new catalog description.
     * @return True if the catalog has been modified.
     */
    @Override
    public boolean update(final Catalog catalog, final CatalogDesc desc) {
        final var hasUpdatedTitle = this.updateTitle(catalog, desc.getTitle());
        final var hasUpdatedDescription = this.updateDescription(catalog,
                desc.getDescription());

        return hasUpdatedTitle || hasUpdatedDescription;
    }

    private boolean updateTitle(final Catalog catalog, final String title) {
        final var newTitle = MetadataUtils.updateString(catalog.getTitle(),
                title, "");
        newTitle.ifPresent(catalog::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateDescription(final Catalog catalog,
                                     final String description) {
        final var newDescription =
                MetadataUtils.updateString(catalog.getDescription(),
                description, "");
        newDescription.ifPresent(catalog::setDescription);

        return newDescription.isPresent();
    }
}
