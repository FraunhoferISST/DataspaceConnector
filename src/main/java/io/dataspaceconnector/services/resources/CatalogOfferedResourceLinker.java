package io.dataspaceconnector.services.resources;

import java.util.List;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.OfferedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the relation between a catalog and its offered resources.
 */
@Service
@NoArgsConstructor
public class CatalogOfferedResourceLinker extends AbstractCatalogResourceLinker<OfferedResource> {
    @Override
    protected List<OfferedResource> getInternal(final Catalog owner) {
        return owner.getOfferedResources();
    }
}
