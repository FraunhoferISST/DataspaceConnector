package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

public abstract class CatalogResourceLinker<T extends Resource> extends BaseUniDirectionalLinkerService<
        Catalog, T, CatalogService, ResourceService<T, ?>> {
}

@Service
final class CatalogOfferedResourceLinker extends CatalogResourceLinker<OfferedResource> {

    @Override
    protected List<OfferedResource> getInternal(final Catalog owner) {
        // TODO Make it safe
        return owner.getOfferedResources();
    }
}

@Service
final class CatalogRequestedResourceLinker extends CatalogResourceLinker<RequestedResource> {

    @Override
    protected List<RequestedResource> getInternal(final Catalog owner) {
        // TODO Make it safe
        return owner.getRequestedResources();
    }
}
