/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.truststore;

import java.net.URI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new TruststoreDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var truststore = factory.create(new TruststoreDesc());

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), truststore.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new TruststoreDesc();
        final var truststore = factory.create(new TruststoreDesc());

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(TruststoreFactory.DEFAULT_LOCATION, truststore.getLocation());
    }

    @Test
    void update_newPassword_willUpdate() {
        /* ARRANGE */
        final var desc = new TruststoreDesc();
        desc.setPassword("A wild password");
        final var truststore = factory.create(new TruststoreDesc());

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getPassword(), truststore.getPassword());
    }

    @Test
    void update_samePassword_willNotUpdate() {
        /* ARRANGE */
        final var password = "password";
        final var desc = new TruststoreDesc();
        desc.setPassword(password);
        final var truststore = factory.create(desc);

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(password, truststore.getPassword());
    }

    @Test
    void update_trustStorePasswordNotNullAndPasswordNull_willNotUpdate() {
        /* ARRANGE */
        final var password = "password";
        final var desc = new TruststoreDesc();
        desc.setPassword(password);
        final var truststore = factory.create(desc);

        /* ACT */
        final var result = factory.update(truststore, new TruststoreDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(password, truststore.getPassword());
    }

    @Test
    void update_differentAlias_willUpdate() {
        /* ARRANGE */
        final var truststore = factory.create(new TruststoreDesc());
        final var desc = new TruststoreDesc();
        desc.setAlias("alias");

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getAlias(), truststore.getAlias());
    }

    @Test
    void update_sameAlias_willNotUpdate() {
        /* ARRANGE */
        final var alias = "alias";
        final var desc = new TruststoreDesc();
        desc.setAlias(alias);
        final var truststore = factory.create(desc);

        /* ACT */
        final var result = factory.update(truststore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(alias, truststore.getAlias());
    }

    @Test
    void update_trustStoreAliasNotNullAndAliasNull_willNotUpdate() {
        /* ARRANGE */
        final var alias = "alias";
        final var desc = new TruststoreDesc();
        desc.setAlias(alias);
        final var truststore = factory.create(desc);

        /* ACT */
        final var result = factory.update(truststore, new TruststoreDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(alias, truststore.getAlias());
    }
}
