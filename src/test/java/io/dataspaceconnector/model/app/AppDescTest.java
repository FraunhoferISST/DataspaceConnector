package io.dataspaceconnector.model.app;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AppDescTest {
    @Test
    void testEquals_valid() {
        EqualsVerifier.simple().forClass(AppDesc.class);
    }
}
