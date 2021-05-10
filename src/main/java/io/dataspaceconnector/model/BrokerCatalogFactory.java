package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class BrokerCatalogFactory implements AbstractFactory<BrokerCatalog, BrokerCatalogDesc> {

    @Override
    public BrokerCatalog create(BrokerCatalogDesc desc) {
        return null;
    }

    @Override
    public boolean update(BrokerCatalog entity, BrokerCatalogDesc desc) {
        return false;
    }
}
