package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class AppStoreCatalogFactory implements AbstractFactory<AppStoreCatalog, AppStoreCatalogDesc> {

    @Override
    public AppStoreCatalog create(AppStoreCatalogDesc desc) {
        return null;
    }

    @Override
    public boolean update(AppStoreCatalog entity, AppStoreCatalogDesc desc) {
        return false;
    }
}
