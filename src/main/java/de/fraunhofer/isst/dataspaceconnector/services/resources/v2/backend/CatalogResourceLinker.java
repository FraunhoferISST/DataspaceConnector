package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

public abstract class CatalogResourceLinker<T extends Resource> extends BaseUniDirectionalLinkerService<
        Catalog, T, CatalogService, ResourceService<T, ?>> {

    @Override
    protected Map<UUID, T> getInternal(final Catalog owner) {
        // TODO Make it safe
        return (Map<UUID, T>) owner.getResources();
    }
}

@Service
final class CatalogOfferedResourceLinker extends CatalogResourceLinker<OfferedResource> {
}

@Service
final class CatalogRequestedResourceLinker extends CatalogResourceLinker<RequestedResource> {
}
