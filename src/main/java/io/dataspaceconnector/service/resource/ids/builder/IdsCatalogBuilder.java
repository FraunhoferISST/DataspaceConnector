/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts dsc catalogs to ids catalogs.
 */
@Component
public final class IdsCatalogBuilder extends AbstractIdsBuilder<Catalog, ResourceCatalog> {

    /**
     * The builder for ids resource (from offered resource).
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> resourceBuilder;

    /**
     * Constructs an IdsCatalogBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param idsResourceBuilder the resource builder.
     */
    @Autowired
    public IdsCatalogBuilder(final SelfLinkHelper selfLinkHelper,
                             final IdsResourceBuilder<OfferedResource> idsResourceBuilder) {
        super(selfLinkHelper);
        this.resourceBuilder = idsResourceBuilder;
    }

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
