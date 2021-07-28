package io.dataspaceconnector.model.endpoint;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AppEndpointFactoryTest {

    final AppEndpointFactory factory = new AppEndpointFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), endpoint.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_URI, endpoint.getLocation());
    }

    @Test
    void update_newDocs_willUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setDocs(URI.create("https://someDocs"));
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getDocs(), endpoint.getDocs());
    }

    @Test
    void update_sameDocs_willNotUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_URI, endpoint.getDocs());
    }

    @Test
    void update_newInfos_willUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setInfo("info");
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getInfo(), endpoint.getInfo());
    }

    @Test
    void update_sameInfos_willNotUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_INFORMATION, endpoint.getInfo());
    }
    @Test
    void update_newAdditionals_willUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setAdditional(Map.of("info", "info"));
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getAdditional(), endpoint.getAdditional());
    }

    @Test
    void update_sameAdditional_willNotUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        final var endpoint = factory.create(new AppEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(0, endpoint.getAdditional().size());
    }

}
