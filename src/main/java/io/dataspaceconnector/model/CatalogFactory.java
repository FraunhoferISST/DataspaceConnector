/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        if (desc.getBootstrapId() != null) {
            catalog.setBootstrapId(URI.create(desc.getBootstrapId()));
        }

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

        final var hasUpdatedTitle =
                this.updateTitle(catalog, desc.getTitle());
        final var hasUpdatedDesc =
                this.updateDescription(catalog, desc.getDescription());
        final boolean hasUpdatedBootstrapId;
        if (desc.getBootstrapId() != null) {
            hasUpdatedBootstrapId =
                    this.updateBootstrapId(catalog, URI.create(desc.getBootstrapId()));
        } else {
            hasUpdatedBootstrapId = false;
        }

        final var hasUpdatedAdditional =
                this.updateAdditional(catalog, desc.getAdditional());

        return hasUpdatedTitle || hasUpdatedDesc || hasUpdatedBootstrapId || hasUpdatedAdditional;
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

    private boolean updateBootstrapId(final Catalog catalog, final URI bootstrapId) {
        final Optional<URI> newBootstrapId;
        if (bootstrapId == null && catalog.getBootstrapId() == null) {
            newBootstrapId = Optional.empty();
        } else {
            newBootstrapId = MetadataUtils
                    .updateUri(
                            catalog.getBootstrapId(),
                            bootstrapId,
                            catalog.getBootstrapId());
        }

        newBootstrapId.ifPresent(catalog::setBootstrapId);

        return newBootstrapId.isPresent();
    }

}
