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
package io.dataspaceconnector.services.resources;

import java.util.List;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.Resource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Base class for handling catalog-resource relations.
 * @param <T> The resource type.
 */
public abstract class AbstractCatalogResourceLinker<T extends Resource>
        extends OwningRelationService<Catalog, T, CatalogService, ResourceService<T, ?>> {
    /**
     * Default constructor.
     */
    protected AbstractCatalogResourceLinker() {
        super();
    }
}

/**
 * Handles the relation between a catalog and its offered resources.
 */
@Service
@NoArgsConstructor
class CatalogOfferedResourceLinker extends AbstractCatalogResourceLinker<OfferedResource> {
    @Override
    protected List<OfferedResource> getInternal(final Catalog owner) {
        return owner.getOfferedResources();
    }
}

/**
 * Handles the relation between a catalog and its requested resources.
 */
@Service
@NoArgsConstructor
class CatalogRequestedResourceLinker extends AbstractCatalogResourceLinker<RequestedResource> {
    @Override
    protected List<RequestedResource> getInternal(final Catalog owner) {
        return owner.getRequestedResources();
    }
}
