/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model.endpoint;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppEndpointFactoryTest {

    final AppEndpointFactory factory = new AppEndpointFactory();

    @Test
    void create_emptyDesc_returnNew() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setLanguage("language");
        desc.setEndpointPort(6666);
        desc.setEndpointType("generic");
        desc.setProtocol("http");
        desc.setMediaType("json");
        desc.setDocs(URI.create("https://docs"));
        desc.setInfo("info");
        desc.setLocation("https://location");

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(desc.getLanguage(), result.getLanguage());
        assertEquals(desc.getEndpointPort(), result.getEndpointPort());
        assertEquals(desc.getEndpointType(), result.getEndpointType());
        assertEquals(desc.getProtocol(), result.getProtocol());
        assertEquals(desc.getMediaType(), result.getMediaType());
        assertEquals(desc.getDocs(), result.getDocs());
        assertEquals(desc.getInfo(), result.getInfo());
        assertEquals(desc.getLocation(), result.getLocation());
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new AppEndpointDesc();
        desc.setLocation("https://someLocation");
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
        assertEquals(AppEndpointFactory.DEFAULT_LOCATION, endpoint.getLocation());
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
        assertEquals(AppEndpointFactory.DEFAULT_URI, endpoint.getDocs());
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
        assertEquals(AppEndpointFactory.DEFAULT_INFORMATION, endpoint.getInfo());
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
