package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ClearingHouseFactory implements AbstractFactory<ClearingHouse, ClearingHouseDesc> {

    @Override
    public ClearingHouse create(ClearingHouseDesc desc) {
        return null;
    }

    @Override
    public boolean update(ClearingHouse entity, ClearingHouseDesc desc) {
        return false;
    }
}
