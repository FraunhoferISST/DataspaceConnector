package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class CatalogViewer implements BaseViewer<Catalog, CatalogView> {
    @Autowired
    private EndpointService endpointService;

    @Override
    public CatalogView create(final Catalog catalog) {
        final var view = new CatalogView();
        view.setTitle(catalog.getTitle());
        view.setDescription(catalog.getDescription());

        final var allResourceIds = catalog.getResources().keySet();
        final var allResourceEndpoints = new HashSet<EndpointId>();

        for(final var resourceId : allResourceIds) {
            allResourceEndpoints.addAll(endpointService.getByEntity(resourceId));
        }

        view.setResources(allResourceEndpoints);

        return view;
    }
}
