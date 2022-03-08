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
package io.dataspaceconnector.controller.resource.view.subscription;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.controller.resource.type.SubscriptionController;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.model.subscription.SubscriptionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {
        SubscriptionViewAssembler.class
})
public class SubscriptionViewAssemblerTest {

    @MockBean
    private ConnectorConfig connectorConfig;

    @Autowired
    private SubscriptionViewAssembler subscriptionViewAssembler;

    @SpyBean
    private SubscriptionFactory subscriptionFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = SubscriptionController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = subscriptionViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> subscriptionViewAssembler.toModel(null));
    }

    @Test
    public void toModel_noResources_returnRepresentationViewWithOffersLink() {
        /* ARRANGE */
        final var subscription = getSubscription();

        /* ACT */
        final var result = subscriptionViewAssembler.toModel(subscription);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(subscription.getSubscriber(), result.getSubscriber());
        Assertions.assertEquals(subscription.getTarget(), result.getTarget());
        Assertions.assertEquals(subscription.getLocation(), result.getLocation());
        Assertions.assertEquals(subscription.isPushData(), result.isPushData());
        Assertions.assertEquals(subscription.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(subscription.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(subscription.getAdditional(), result.getAdditional());
    }

    private Subscription getSubscription() {
        final var desc = new SubscriptionDesc();
        desc.setIdsProtocol(true);
        desc.setPushData(false);
        desc.setTarget(URI.create("https://target"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setLocation(URI.create("https://url"));
        final var subscription = subscriptionFactory.create(desc);

        final var additional = new HashMap<String, String>();
        additional.put("key", "value");
        final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

        ReflectionTestUtils.setField(subscription, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(subscription, "creationDate", date);
        ReflectionTestUtils.setField(subscription, "modificationDate", date);
        ReflectionTestUtils.setField(subscription, "additional", additional);

        return subscription;
    }
}
