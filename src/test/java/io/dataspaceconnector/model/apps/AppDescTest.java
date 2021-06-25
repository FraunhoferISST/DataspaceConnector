package io.dataspaceconnector.model.apps;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AppDescTest {
    @Test
    void testEquals_valid() {
        EqualsVerifier.simple().forClass(AppDesc.class);
    }
}
