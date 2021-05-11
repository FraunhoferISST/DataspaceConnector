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
package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.model.RequestedResourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {SubscriberNotificationService.class, RequestedResourceFactory.class})
public class SubscriberNotificationServiceTest {

    @MockBean
    private RequestedResourceService requestedResourceService;

    @Autowired
    private SubscriberNotificationService subscriberNotificationService;

    @Autowired
    private RequestedResourceFactory requestedResourceFactory;

    private final UUID resourceId = UUID.randomUUID();
    private final URI subscriber1 = URI.create("https://subscriber-1.com");
    private final URI subscriber2 = URI.create("https://subscriber-2.com");

    @Test
    public void addSubscription_resourceIdNull_throwIllegalArgumentException() {
        /* ARRANGE */
        when(requestedResourceService.get(null)).thenThrow(IllegalArgumentException.class);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () ->
                subscriberNotificationService.addSubscription(null, subscriber1));
    }

    @Test
    public void addSubscription_urlNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () ->
                subscriberNotificationService.addSubscription(resourceId, null));
    }

    @Test
    public void addSubscription_noSubscriptionsPresent_addUrlToSubscribers() {
        /* ARRANGE */
        final var resource = getRequestedResource();

        when(requestedResourceService.get(resourceId)).thenReturn(resource);
        when(requestedResourceService.updateSubscriptions(eq(resourceId), any())).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.addSubscription(resourceId, subscriber1);

        /* ASSERT */
        verify(requestedResourceService, times(1))
                .updateSubscriptions(resourceId, List.of(subscriber1));
    }

    @Test
    public void addSubscription_subscriptionsPresent_addUrlToSubscribers() {
        /* ARRANGE */
        final var resource = getRequestedResourceWithSubscriptions();
        assertFalse(resource.getSubscribers().contains(subscriber1));

        when(requestedResourceService.get(resourceId)).thenReturn(resource);
        when(requestedResourceService.updateSubscriptions(eq(resourceId), any())).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.addSubscription(resourceId, subscriber1);

        /* ASSERT */
        final var subscribers = resource.getSubscribers();
        subscribers.add(subscriber1);

        verify(requestedResourceService, times(1))
                .updateSubscriptions(resourceId, subscribers);
    }

    @Test
    public void addSubscription_urlAlreadySubscribed_doNothing() {
        /* ARRANGE */
        final var resource = getRequestedResourceWithSubscriptions();

        when(requestedResourceService.get(resourceId)).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.addSubscription(resourceId, subscriber2);

        /* ASSERT */
        verify(requestedResourceService, never())
                .updateSubscriptions(eq(resourceId), any());
    }

    @Test
    public void removeSubscription_resourceIdNull_throwIllegalArgumentException() {
        /* ARRANGE */
        when(requestedResourceService.get(null)).thenThrow(IllegalArgumentException.class);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () ->
                subscriberNotificationService.removeSubscription(null, subscriber1));
    }

    @Test
    public void removeSubscription_urlNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () ->
                subscriberNotificationService.removeSubscription(resourceId, null));
    }

    @Test
    public void removeSubscription_noSubscriptionsPresent_doNothing() {
        /* ARRANGE */
        final var resource = getRequestedResource();

        when(requestedResourceService.get(resourceId)).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.removeSubscription(resourceId, subscriber1);

        /* ASSERT */
        verify(requestedResourceService, never())
                .updateSubscriptions(eq(resourceId), any());
    }

    @Test
    public void removeSubscription_urlNotSubscribed_doNothing() {
        /* ARRANGE */
        final var resource = getRequestedResourceWithSubscriptions();

        when(requestedResourceService.get(resourceId)).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.removeSubscription(resourceId, subscriber1);

        /* ASSERT */
        verify(requestedResourceService, never())
                .updateSubscriptions(eq(resourceId), any());
    }

    @Test
    public void removeSubscription_urlSubscribed_removeUrlFromSubscribers() {
        /* ARRANGE */
        final var resource = getRequestedResourceWithSubscriptions();

        when(requestedResourceService.get(resourceId)).thenReturn(resource);
        when(requestedResourceService.updateSubscriptions(eq(resourceId), any())).thenReturn(resource);

        /* ACT */
        subscriberNotificationService.removeSubscription(resourceId, subscriber2);

        /* ASSERT */
        verify(requestedResourceService, times(1))
                .updateSubscriptions(resourceId, new ArrayList<>());
    }

    @Test
    public void notifySubscribers_reosurceNotFound_doNothing() {
        /* ARRANGE */
        final var remoteId = URI.create("https://remote-id.com");
        when(requestedResourceService.identifyByRemoteId(remoteId))
                .thenReturn(Optional.empty());

        final var threadsBefore = Thread.getAllStackTraces().keySet();

        /* ACT */
        subscriberNotificationService.notifySubscribers(remoteId);

        /* ARRANGE */
        final var threadsAfter = Thread.getAllStackTraces().keySet();
        assertEquals(threadsBefore.size(), threadsAfter.size());
    }

    @Test
    public void notifySubscribers_noSubscriptions_doNothing() {
        /* ARRANGE */
        final var remoteId = URI.create("https://remote-id.com");
        final var resource = getRequestedResource();

        when(requestedResourceService.identifyByRemoteId(remoteId))
                .thenReturn(Optional.of(resourceId));
        when(requestedResourceService.get(resourceId)).thenReturn(resource);

        final var threadsBefore = Thread.getAllStackTraces().keySet();

        /* ACT */
        subscriberNotificationService.notifySubscribers(remoteId);

        /* ARRANGE */
        final var threadsAfter = Thread.getAllStackTraces().keySet();
        assertEquals(threadsBefore.size(), threadsAfter.size());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private RequestedResource getRequestedResource() {
        final var desc = new RequestedResourceDesc();
        desc.setLanguage("EN");
        desc.setTitle("title");
        desc.setDescription("description");
        desc.setKeywords(Collections.singletonList("keyword"));
        desc.setEndpointDocumentation(URI.create("https://endpointDocumentation.com"));
        desc.setLicence(URI.create("https://license.com"));
        desc.setPublisher(URI.create("https://publisher.com"));
        desc.setSovereign(URI.create("https://sovereign.com"));
        desc.setRemoteId(URI.create("https://remote-id.com"));
        final var resource = requestedResourceFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(resource, "id", resourceId);
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);
        ReflectionTestUtils.setField(resource, "additional", additional);

        return resource;
    }

    private RequestedResource getRequestedResourceWithSubscriptions() {
        final var resource = getRequestedResource();

        final var subscriberList = new ArrayList<URI>();
        subscriberList.add(subscriber2);
        resource.setSubscribers(subscriberList);

        return resource;
    }

}
