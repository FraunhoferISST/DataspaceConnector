package io.dataspaceconnector.model.broker;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class BrokerDescTest {
    @Test
    void testEquals_valid() {
        EqualsVerifier.simple().forClass(BrokerDesc.class);
    }
}
