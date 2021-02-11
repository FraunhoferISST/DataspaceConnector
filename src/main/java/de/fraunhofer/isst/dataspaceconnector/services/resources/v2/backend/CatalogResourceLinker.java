package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CatalogResourceLinker extends BaseUniDirectionalLinkerService<
        Catalog, Resource, CatalogService, ResourceService> {

    @Override
    protected Map<UUID, Resource> getInternal(final Catalog owner) {
        return owner.getResources();
    }
}
