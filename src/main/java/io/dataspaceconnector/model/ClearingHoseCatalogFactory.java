package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ClearingHoseCatalogFactory implements AbstractFactory<ClearingHouseCatalog, ClearingHouseCatalogDesc> {

    @Override
    public ClearingHouseCatalog create(ClearingHouseCatalogDesc desc) {
        return null;
    }

    @Override
    public boolean update(ClearingHouseCatalog entity, ClearingHouseCatalogDesc desc) {
        return false;
    }
}
