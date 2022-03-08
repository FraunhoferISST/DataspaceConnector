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
package io.dataspaceconnector.model.resource;

import java.util.ArrayList;
import java.util.Collections;

import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfferedResourceTest {

    @Test
    void getCatalogs_catalogsNull_returnNull() {
        /* ARRANGE */
        final var resource = new OfferedResource();

        /* ACT */
        final var result = resource.getCatalogs();

        /* ASSERT */
        assertNull(result);
    }

    @Test
    void getCatalogs_catalogsEmpty_returnEmptyList() {
        /* ARRANGE */
        final var resource = new OfferedResource();
        resource.setCatalogs(new ArrayList<>());

        /* ACT */
        final var result = resource.getCatalogs();

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getCatalogs_catalogsNotEmpty_returnList() {
        /* ARRANGE */
        final var catalog = new Catalog();
        final var resource = new OfferedResource();
        resource.setCatalogs(Collections.singletonList(catalog));

        /* ACT */
        final var result = resource.getCatalogs();

        /* ASSERT */
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(catalog, result.get(0));
    }

    @Test
    void getBrokers_brokersNull_returnNull() {
        /* ARRANGE */
        final var resource = new OfferedResource();

        /* ACT */
        final var result = resource.getBrokers();

        /* ASSERT */
        assertNull(result);
    }

    @Test
    void getBrokers_brokersEmpty_returnEmptyList() {
        /* ARRANGE */
        final var resource = new OfferedResource();
        resource.setBrokers(new ArrayList<>());

        /* ACT */
        final var result = resource.getBrokers();

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBrokers_brokersNotEmpty_returnList() {
        /* ARRANGE */
        final var broker = new Broker();
        final var resource = new OfferedResource();
        resource.setBrokers(Collections.singletonList(broker));

        /* ACT */
        final var result = resource.getBrokers();

        /* ASSERT */
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(broker, result.get(0));
    }

    @Test
    public void equalsAndHash_will_pass() {
        final var c1 = new Catalog();
        final var c2 = new Catalog();
        ReflectionTestUtils.setField(c2, "title", "haha");

        final var r1 = new Representation();
        final var r2 = new Representation();
        ReflectionTestUtils.setField(r2, "title", "haha");

        final var co1 = new Contract();
        final var co2 = new Contract();
        ReflectionTestUtils.setField(co2, "title", "haha");

        final var b1 = new Broker();
        final var b2 = new Broker();
        ReflectionTestUtils.setField(b2, "title", "haha");

        EqualsVerifier.simple().forClass(OfferedResource.class)
                      .withPrefabValues(Catalog.class, c1, c2)
                      .withPrefabValues(Representation.class, r1, r2)
                      .withPrefabValues(Contract.class, co1, co2)
                      .withPrefabValues(Broker.class, b1, b2)
                      .withIgnoredFields("id")
                      .verify();
    }
}
