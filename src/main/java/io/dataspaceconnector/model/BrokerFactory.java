package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class BrokerFactory implements AbstractFactory<Broker, BrokerDesc> {

    @Override
    public Broker create(BrokerDesc desc) {
        return null;
    }

    @Override
    public boolean update(Broker entity, BrokerDesc desc) {
        return false;
    }
}
