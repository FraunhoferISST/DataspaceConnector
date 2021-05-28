package io.dataspaceconnector.services.resources;

import java.util.List;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.RequestedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the relation between a catalog and its requested resources.
 */
@Service
@NoArgsConstructor
public class CatalogRequestedResourceLinker
        extends AbstractCatalogResourceLinker<RequestedResource> {
    @Override
    protected List<RequestedResource> getInternal(final Catalog owner) {
        return owner.getRequestedResources();
    }
}
