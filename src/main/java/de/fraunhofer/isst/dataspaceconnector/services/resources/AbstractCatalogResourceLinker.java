package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
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
