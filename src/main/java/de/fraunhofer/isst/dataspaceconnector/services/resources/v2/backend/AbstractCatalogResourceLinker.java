package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

public abstract class AbstractCatalogResourceLinker<T extends Resource>
        extends BaseUniDirectionalLinkerService<Catalog, T, CatalogService, ResourceService<T, ?>> {
    protected AbstractCatalogResourceLinker() {
        super();
    }
}

@Service
class CatalogOfferedResourceLinker extends AbstractCatalogResourceLinker<OfferedResource> {
    public CatalogOfferedResourceLinker() {
        super();
    }

    @Override
    protected List<OfferedResource> getInternal(final Catalog owner) {
        return owner.getOfferedResources();
    }
}

@Service
class CatalogRequestedResourceLinker extends AbstractCatalogResourceLinker<RequestedResource> {
    public CatalogRequestedResourceLinker() {
        super();
    }

    @Override
    protected List<RequestedResource> getInternal(final Catalog owner) {
        return owner.getRequestedResources();
    }
}
