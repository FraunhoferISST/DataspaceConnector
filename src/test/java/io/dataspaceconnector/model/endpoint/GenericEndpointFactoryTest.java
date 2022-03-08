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

import io.dataspaceconnector.model.datasource.DataSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericEndpointFactoryTest {

    final GenericEndpointFactory factory = new GenericEndpointFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        desc.setLocation("https://someLocation");
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), endpoint.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_LOCATION, endpoint.getLocation());
    }

    @Test
    void update_newDocs_willUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        desc.setDocs(URI.create("https://someDocs"));
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getDocs(), endpoint.getDocs());
    }

    @Test
    void update_sameDocs_willNotUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_URI, endpoint.getDocs());
    }

    @Test
    void update_newInfos_willUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        desc.setInfo("info");
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getInfo(), endpoint.getInfo());
    }

    @Test
    void update_sameInfos_willNotUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(GenericEndpointFactory.DEFAULT_INFORMATION, endpoint.getInfo());
    }
    @Test
    void update_newAdditionals_willUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        desc.setAdditional(Map.of("info", "info"));
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getAdditional(), endpoint.getAdditional());
    }

    @Test
    void update_sameAdditional_willNotUpdate() {
        /* ARRANGE */
        final var desc = new GenericEndpointDesc();
        final var endpoint = factory.create(new GenericEndpointDesc());

        /* ACT */
        final var result = factory.update(endpoint, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(0, endpoint.getAdditional().size());
    }

    @Test
    void setDataSourceToGenericEndpoint_willSetDataSource() {
        /* ARRANGE */
        final var endpoint = new GenericEndpoint();
        final var dataSource = new DataSource();

        /* ACT */
        final var result =factory.setDataSourceToGenericEndpoint(endpoint, dataSource);

        /* ASSERT */
        assertEquals(dataSource, result.getDataSource());
    }

    @Test
    void removeDataSource_willRemoveDataSource() {
        /* ARRANGE */
        final var endpoint = new GenericEndpoint();
        final var dataSource = new DataSource();
        endpoint.setDataSource(dataSource);

        /* ACT */
        final var result = factory.removeDataSource(endpoint);

        /* ASSERT */
        assertNull(result.getDataSource());
    }
}
