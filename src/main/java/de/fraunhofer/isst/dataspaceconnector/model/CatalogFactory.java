package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a catalog.
 */
@Component
public class CatalogFactory implements AbstractFactory<Catalog, CatalogDesc> {

    /**
     * Default title assigned to all catalogs.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * Default description assigned to all catalogs.
     */
    public static final String DEFAULT_DESCRIPTION = "";

    /**
     * Default constructor.
     */
    public CatalogFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new catalog.
     * @param desc The description of the new catalog.
     * @return The new catalog.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public Catalog create(final CatalogDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var catalog = new Catalog();
        catalog.setOfferedResources(new ArrayList<>());
        catalog.setRequestedResources(new ArrayList<>());

        update(catalog, desc);

        return catalog;
    }

    /**
     * Update a catalog.
     * @param catalog The catalog to be updated.
     * @param desc    The new catalog description.
     * @return True if the catalog has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final Catalog catalog, final CatalogDesc desc) {
        Utils.requireNonNull(catalog, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedTitle = this.updateTitle(catalog, desc.getTitle());
        final var hasUpdatedDesc = this.updateDescription(catalog, desc.getDescription());
        final var hasUpdatedAdditional = this.updateAdditional(catalog, desc.getAdditional());

        return hasUpdatedTitle || hasUpdatedDesc || hasUpdatedAdditional;
    }

    private boolean updateTitle(final Catalog catalog, final String title) {
        final var newTitle = MetadataUtils.updateString(catalog.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(catalog::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateDescription(final Catalog catalog, final String description) {
        final var newDescription =
                MetadataUtils
                        .updateString(catalog.getDescription(), description, DEFAULT_DESCRIPTION);
        newDescription.ifPresent(catalog::setDescription);

        return newDescription.isPresent();
    }

    private boolean updateAdditional(final Catalog catalog, final Map<String, String> additional) {
        final var newAdditional =
                MetadataUtils.updateStringMap(catalog.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(catalog::setAdditional);

        return newAdditional.isPresent();
    }
}
