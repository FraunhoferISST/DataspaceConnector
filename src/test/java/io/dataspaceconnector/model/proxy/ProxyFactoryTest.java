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
package io.dataspaceconnector.model.proxy;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProxyFactoryTest {

    final ProxyDesc desc = new ProxyDesc();
    final ProxyFactory factory = new ProxyFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var location = URI.create("https://localhost:8080");
        final var exclusion = "https://localhost:8081";
        desc.setLocation(location);
        desc.setExclusions(List.of(exclusion));

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(location, result.getLocation());
        assertFalse(result.getExclusions().isEmpty());
        assertEquals(exclusion, result.getExclusions().get(0));
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new ProxyDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), proxy.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ProxyDesc();
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(ProxyFactory.DEFAULT_LOCATION, proxy.getLocation());
    }

    @Test
    void update_newExclusionList_willUpdate() {
        /* ARRANGE */
        final var desc = new ProxyDesc();
        desc.setExclusions(List.of("exclusion"));
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getExclusions(), proxy.getExclusions());
    }

    @Test
    void update_sameExclusionList_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ProxyDesc();
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, desc);

        /* ASSERT */
        assertFalse(result);
        assertTrue(proxy.getExclusions().isEmpty());
    }

    @Test
    void update_newAuth_willUpdate() {
        /* ARRANGE */
        final var desc = new ProxyDesc();
        desc.setAuthentication(new AuthenticationDesc("", ""));
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(new BasicAuth(desc.getAuthentication()), proxy.getAuthentication());
    }

    @Test
    void update_sameAuth_willNotUpdate() {
        /* ARRANGE */
        final var proxy = factory.create(new ProxyDesc());

        /* ACT */
        final var result = factory.update(proxy, new ProxyDesc());

        /* ASSERT */
        assertFalse(result);
        assertNull(proxy.getAuthentication());
    }
}
