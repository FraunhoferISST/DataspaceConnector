package io.dataspaceconnector.model.truststore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TruststoreFactoryTest {

    final TruststoreDesc desc = new TruststoreDesc();
    final TruststoreFactory factory = new TruststoreFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }
}
