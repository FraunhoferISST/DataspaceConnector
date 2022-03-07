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
package io.dataspaceconnector.model.keystore;

import java.net.URI;

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

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new KeystoreDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var keystore = factory.create(new KeystoreDesc());

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), keystore.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new KeystoreDesc();
        final var keystore = factory.create(new KeystoreDesc());

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(KeystoreFactory.DEFAULT_LOCATION, keystore.getLocation());
    }

    @Test
    void update_newPassword_willUpdate() {
        /* ARRANGE */
        final var desc = new KeystoreDesc();
        desc.setPassword("A wild password");
        final var keystore = factory.create(new KeystoreDesc());

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getPassword(), keystore.getPassword());
    }

    @Test
    void update_samePassword_willNotUpdate() {
        /* ARRANGE */
        final var password = "password";
        final var desc = new KeystoreDesc();
        desc.setPassword(password);
        final var keystore = factory.create(desc);

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(password, keystore.getPassword());
    }

    @Test
    void update_keyStorePasswordNotNullAndPasswordNull_willNotUpdate() {
        /* ARRANGE */
        final var password = "password";
        final var desc = new KeystoreDesc();
        desc.setPassword(password);
        final var keystore = factory.create(desc);

        /* ACT */
        final var result = factory.update(keystore, new KeystoreDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(password, keystore.getPassword());
    }

    @Test
    void update_differentAlias_willUpdate() {
        /* ARRANGE */
        final var keystore = factory.create(new KeystoreDesc());
        final var desc = new KeystoreDesc();
        desc.setAlias("alias");

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getAlias(), keystore.getAlias());
    }

    @Test
    void update_sameAlias_willNotUpdate() {
        /* ARRANGE */
        final var alias = "alias";
        final var desc = new KeystoreDesc();
        desc.setAlias(alias);
        final var keystore = factory.create(desc);

        /* ACT */
        final var result = factory.update(keystore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(alias, keystore.getAlias());
    }

    @Test
    void update_keyStoreAliasNotNullAndAliasNull_willNotUpdate() {
        /* ARRANGE */
        final var alias = "alias";
        final var desc = new KeystoreDesc();
        desc.setAlias(alias);
        final var keystore = factory.create(desc);

        /* ACT */
        final var result = factory.update(keystore, new KeystoreDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(alias, keystore.getAlias());
    }
}
