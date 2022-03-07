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

import io.dataspaceconnector.controller.resource.type.SubscriptionController;
import io.dataspaceconnector.model.subscription.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {SubscriptionViewAssembler.class})
public class SubscriptionViewAssemblerTest2 {

    @Autowired
    private SubscriptionViewAssembler viewAssembler;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = SubscriptionController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = viewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var subscriberId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = SubscriptionController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = viewAssembler.getSelfLink(subscriberId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + subscriberId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> viewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnSubscriberView() {
        /* ARRANGE */
        final var subscriber = getSubscriber();

        /* ACT */
        final var result = viewAssembler.toModel(subscriber);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(subscriber.getLocation(), result.getLocation());
        assertEquals(subscriber.getCreationDate(), result.getCreationDate());
        assertEquals(subscriber.getModificationDate(), result.getModificationDate());
        assertEquals(subscriber.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getSubscriberLink(subscriber.getId()), selfLink.get().getHref());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Subscription getSubscriber() {
        final var location = URI.create("http://valid-url.com");
        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var subscriber = new Subscription();
        ReflectionTestUtils.setField(subscriber, "location", location);
        ReflectionTestUtils.setField(subscriber, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(subscriber, "creationDate", date);
        ReflectionTestUtils.setField(subscriber, "modificationDate", date);
        ReflectionTestUtils.setField(subscriber, "additional", additional);

        return subscriber;
    }

    private String getSubscriberLink(final UUID subscriberId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = SubscriptionController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + subscriberId;
    }

}
