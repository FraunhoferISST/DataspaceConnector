package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CatalogFactory implements BaseFactory<Catalog, CatalogDesc> {
    @Override
    public Catalog create(final CatalogDesc desc) {
        var catalog = new Catalog();
        catalog.setResources(new HashMap<>());

        update(catalog, desc);

        return catalog;
    }

    @Override
    public boolean update(final Catalog catalog, final CatalogDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (newTitle.equals(catalog.getTitle())) {
            catalog.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        var newCatalog = desc.getDescription() != null
                ? desc.getDescription() : "";
        if (newCatalog.equals(catalog.getDescription())) {
            catalog.setDescription(newCatalog);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
