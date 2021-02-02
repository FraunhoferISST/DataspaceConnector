package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CatalogResourceLinker extends BaseUniDirectionalLinkerService<
        Catalog, CatalogDesc, Resource, ResourceDesc, CatalogService,
        ResourceService> {
    @Override
    protected Map<UUID, Resource> getInternal(final Catalog owner) {
        return owner.getResources();
    }
}
