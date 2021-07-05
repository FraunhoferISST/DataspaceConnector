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

import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.model.RequestedResourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void notifySubscribers_resourceNotFound_doNothing() {
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
        desc.setLicense(URI.create("https://license.com"));
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

}
