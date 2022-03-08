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
package io.dataspaceconnector.controller.message;

import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {NotificationController.class})
class NotificationControllerTest {

    @MockBean
    private SubscriptionService subscriptionSvc;

    @MockBean
    private EntityResolver entityResolver;

    @MockBean
    private SubscriberNotificationService subscriberNotificationSvc;

    @Autowired
    private NotificationController controller;

    private final URI elementId = URI.create("https://someElement");

    @Test
    void sendMessage_validUri_returnOk() {
        /* ARRANGE */
        final var entity = new Representation();
        Mockito.doReturn(Optional.of(entity)).when(entityResolver).getEntityById(elementId);

        final var subscriptions = new ArrayList<Subscription>();
        subscriptions.add(new Subscription());
        Mockito.doReturn(subscriptions).when(subscriptionSvc).getByTarget(elementId);

        /* ACT */
        final var result = controller.sendMessage(elementId);

        /* ASSERT */
        Mockito.verify(subscriberNotificationSvc, Mockito.atLeastOnce())
               .notifyAll(eq(subscriptions), eq(elementId), eq(entity));
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCode().value());
    }

    @Test
    void sendMessage_elementNotFound_returnNotFound() {
        /* ARRANGE */
        Mockito.doReturn(Optional.empty()).when(entityResolver).getEntityById(elementId);

        /* ACT */
        final var result = controller.sendMessage(elementId);

        /* ASSERT */
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
    }

    @Test
    void sendMessage_elementHasNoSubscriber_returnOk() {
        /* ARRANGE */
        final var entity = new Representation();
        Mockito.doReturn(Optional.of(entity)).when(entityResolver).getEntityById(elementId);

        final var subscriptions = new ArrayList<Subscription>();
        Mockito.doReturn(subscriptions).when(subscriptionSvc).getByTarget(elementId);

        /* ACT */
        final var result = controller.sendMessage(elementId);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCode().value());
    }
}
