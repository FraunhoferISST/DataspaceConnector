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
package io.dataspaceconnector.model.broker;

import java.net.URI;

import io.dataspaceconnector.model.base.RegistrationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrokerFactoryTest {

    final BrokerDesc desc = new BrokerDesc();
    final BrokerFactory factory = new BrokerFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyBroker";
        desc.setTitle(title);
        desc.setStatus(RegistrationStatus.UNREGISTERED);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertEquals(RegistrationStatus.UNREGISTERED, result.getStatus());
        assertTrue(result.getOfferedResources().isEmpty());
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new BrokerDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var broker = factory.create(new BrokerDesc());

        /* ACT */
        final var result = factory.update(broker, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), broker.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new BrokerDesc();
        final var broker = factory.create(new BrokerDesc());

        /* ACT */
        final var result = factory.update(broker, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(BrokerFactory.DEFAULT_URI, broker.getLocation());
    }

    @Test
    void update_newRegistrationStatus_willUpdate() {
        /* ARRANGE */
        final var desc = new BrokerDesc();
        desc.setStatus(RegistrationStatus.REGISTERED);
        final var broker = factory.create(new BrokerDesc());

        /* ACT */
        final var result = factory.update(broker, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getStatus(), broker.getStatus());
    }

    @Test
    void update_sameRegistrationStatus_willNotUpdate() {
        /* ARRANGE */
        final var desc = new BrokerDesc();
        final var broker = factory.create(new BrokerDesc());

        /* ACT */
        final var result = factory.update(broker, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(RegistrationStatus.UNREGISTERED, broker.getStatus());
    }
}
