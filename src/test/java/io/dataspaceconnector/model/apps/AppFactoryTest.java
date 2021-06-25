package io.dataspaceconnector.model.apps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppFactoryTest {

    final AppDesc desc = new AppDesc();
    final AppFactory factory = new AppFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "Random title";
        desc.setTitle(title);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
    }
}
