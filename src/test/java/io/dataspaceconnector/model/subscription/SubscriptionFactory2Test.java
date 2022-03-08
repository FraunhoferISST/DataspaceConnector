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

import java.net.URI;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.config.ConnectorConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionFactory2Test {

    private ConnectorConfig connectorConfig = Mockito.mock(ConnectorConfig.class);

    private SubscriptionFactory factory = new SubscriptionFactory(connectorConfig);

    private URI newUrl = URI.create("http://valid-url.com");

    private URI initialUrl = URI.create("http://valid-url2.com");

    @Test
    public void create_descNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_returnSubscriberWithoutIdDatesAndResources() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(Subscription.class, result.getClass());
        assertEquals(desc.getLocation(), result.getLocation());
        assertEquals(desc.getSubscriber(), result.getSubscriber());
        assertEquals(desc.getTarget(), result.getTarget());
        assertEquals(desc.isIdsProtocol(), result.isIdsProtocol());
        assertEquals(desc.isPushData(), result.isPushData());
        assertNull(result.getCreationDate());
        assertNull(result.getModificationDate());
    }

    @Test
    public void update_subscriberNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> factory.update(null, new SubscriptionDesc()));
    }

    @Test
    public void update_descNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> factory.update(getSubscriber(), null));
    }

    @Test
    public void update_newLocation_returnTrueAndSubscriberUpdates() {
        /* ACT */
        final var subscriber = getSubscriber();
        final var desc = getValidDesc();

        final var location = subscriber.getLocation();

        /* ACT */
        factory.update(subscriber, desc);

        /* ASSERT */
        assertNotEquals(location, subscriber.getLocation());
        assertEquals(desc.getLocation(), subscriber.getLocation());
    }

    @Test
    public void update_descUrlNull_throwInvalidEntityException() {
        /* ACT  && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> factory.update(getSubscriber(), new SubscriptionDesc()));
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private SubscriptionDesc getValidDesc() {
        final var desc = new SubscriptionDesc();
        desc.setTarget(newUrl);
        desc.setLocation(newUrl);
        desc.setSubscriber(newUrl);
        desc.setPushData(true);
        desc.setIdsProtocol(false);
        return desc;
    }

    private Subscription getSubscriber() {
        final var subscriber = new Subscription();
        ReflectionTestUtils.setField(subscriber, "location", initialUrl);
        return subscriber;
    }

}
