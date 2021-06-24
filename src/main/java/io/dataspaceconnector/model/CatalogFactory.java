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

import java.util.ArrayList;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a catalog.
 */
@Component
public class CatalogFactory extends AbstractFactory<Catalog, CatalogDesc> {

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
            catalog.setBootstrapId(desc.getBootstrapId());
        }

        update(catalog, desc);

        return catalog;
    }

    @Override
    protected boolean updateInternal(final Catalog catalog, final CatalogDesc desc) {
        final var hasUpdatedTitle = this.updateTitle(catalog, desc.getTitle());
        final var hasUpdatedDesc = this.updateDescription(catalog, desc.getDescription());

        return hasUpdatedTitle || hasUpdatedDesc;
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
}
