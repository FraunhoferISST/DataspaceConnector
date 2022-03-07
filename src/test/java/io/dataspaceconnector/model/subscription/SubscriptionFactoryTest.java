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
package io.dataspaceconnector.model.subscription;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.config.ConnectorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionFactoryTest {

    private ConnectorConfig connectorConfig = Mockito.mock(ConnectorConfig.class);

    private final SubscriptionDesc desc = new SubscriptionDesc();
    private SubscriptionFactory factory;

    @BeforeEach
    public void init() {
        factory = new SubscriptionFactory(connectorConfig);
    }

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertNotNull(result);
    }

    @Test
    void create_validDesc_throwInvalidEntityException() {
        /* ARRANGE */
        // nothing to arrange

        /* ACT & ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.create(desc));
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://newLocation"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://target"));

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(updatedDesc.getLocation(), subscription.getLocation());
    }

    @Test
    void update_newTarget_willUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://newTarget"));

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(updatedDesc.getTarget(), subscription.getTarget());
    }

    @Test
    void update_newSubscriber_willUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://newSubscriber"));
        updatedDesc.setTarget(URI.create("https://target"));

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(updatedDesc.getSubscriber(), subscription.getSubscriber());
    }

    @Test
    void update_newPushData_willUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://target"));
        updatedDesc.setPushData(true);

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(updatedDesc.isPushData(), subscription.isPushData());
    }

    @Test
    void update_newPushData_willNotUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://target"));

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertFalse(result);
        assertFalse(subscription.isPushData());
    }

    @Test
    void update_newIdsValue_willUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://target"));
        updatedDesc.setIdsProtocol(true);

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(updatedDesc.isIdsProtocol(), subscription.isIdsProtocol());
    }

    @Test
    void update_IdsValue_willNotUpdate() {
        /* ARRANGE */
        final var desc = new SubscriptionDesc();
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        final var updatedDesc = new SubscriptionDesc();
        updatedDesc.setLocation(URI.create("https://location"));
        updatedDesc.setSubscriber(URI.create("https://subscriber"));
        updatedDesc.setTarget(URI.create("https://target"));

        final var subscription = factory.create(desc);

        /* ACT */
        final var result = factory.update(subscription, updatedDesc);

        /* ASSERT */
        assertFalse(result);
        assertFalse(subscription.isIdsProtocol());
    }

}
