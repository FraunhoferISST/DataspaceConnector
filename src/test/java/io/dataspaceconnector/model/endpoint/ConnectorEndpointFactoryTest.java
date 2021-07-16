package io.dataspaceconnector.model.endpoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorEndpointFactoryTest {
    private ConnectorEndpointFactory factory = new ConnectorEndpointFactory();

    @Test
    public void create_validInput_createNew() {
        assertNotNull(factory.create(new ConnectorEndpointDesc()));
    }
}
