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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.service.resource.relation.CatalogOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.CatalogRequestedResourceLinker;
import io.dataspaceconnector.service.resource.type.CatalogService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

/**
 * Builds catalogs from templates.
 */
@RequiredArgsConstructor
public class CatalogTemplateBuilder {

    /**
     * Service for catalogs.
     */
    private final @NonNull CatalogService catalogService;

    /**
     * Linker for catalog - offered resources.
     */
    private final @NonNull CatalogOfferedResourceLinker offeredLinker;

    /**
     * Linker for catalog - requested resources.
     */
    private final @NonNull CatalogRequestedResourceLinker requestedLinker;

    /**
     * Builder for offered resources.
     */
    private final @NonNull OfferedResourceTemplateBuilder offeredBuilder;

    /**
     * Builder for requested resources.
     */
    private final @NonNull RequestedResourceTemplateBuilder requestedBuilder;

    /**
     * Build a catalog and dependencies from a template.
     *
     * @param template The catalog template.
     * @return The new resource.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public Catalog build(final CatalogTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var offeredIds =
                Utils.toStream(template.getOfferedResources()).map(x -> offeredBuilder.build(x)
                        .getId())
                        .collect(Collectors.toSet());

        final var requestedIds =
                Utils.toStream(template.getRequestedResources()).map(x -> requestedBuilder.build(x)
                        .getId())
                        .collect(Collectors.toSet());

        final var catalog = catalogService.create(template.getDesc());
        offeredLinker.replace(catalog.getId(), offeredIds);
        requestedLinker.replace(catalog.getId(), requestedIds);

        return catalog;
    }
}
