package io.dataspaceconnector.model.keystore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeystoreFactoryTest {
    final KeystoreDesc    desc    = new KeystoreDesc();
    final KeystoreFactory factory = new KeystoreFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        // Nothing to arrange here.s

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }
}
