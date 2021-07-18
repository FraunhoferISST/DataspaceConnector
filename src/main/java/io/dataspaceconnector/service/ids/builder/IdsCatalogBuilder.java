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
package io.dataspaceconnector.service.ids.builder;

import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.resource.OfferedResource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts dsc catalogs to ids catalogs.
 */
@Component
@RequiredArgsConstructor
public final class IdsCatalogBuilder extends AbstractIdsBuilder<Catalog, ResourceCatalog> {

    /**
     * The builder for ids resource (from offered resource).
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> resourceBuilder;

    @Override
    protected ResourceCatalog createInternal(final Catalog catalog, final int currentDepth,
                                             final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var resources = create(resourceBuilder,
                catalog.getOfferedResources(), currentDepth, maxDepth);

        final var builder = new ResourceCatalogBuilder(getAbsoluteSelfLink(catalog));
        resources.ifPresent(builder::_offeredResource_);

        return builder.build();
    }
}
