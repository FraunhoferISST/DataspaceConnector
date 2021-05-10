package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class ConnectorFactory implements AbstractFactory<Connector, ConnectorDesc> {

    @Override
    public Connector create(ConnectorDesc desc) {
        return null;
    }

    @Override
    public boolean update(Connector entity, ConnectorDesc desc) {
        return false;
    }
}
