package io.dataspaceconnector.model.app;

import io.dataspaceconnector.model.agreement.AgreementDesc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class AppFactoryTest {

    private AppFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new AppFactory();
    }

    @Test
    public void default_value_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", AppFactory.DEFAULT_VALUE);
    }

    @Test
    public void default_uri_is_app_com() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("https://app.com"), AppFactory.DEFAULT_URI);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

}
