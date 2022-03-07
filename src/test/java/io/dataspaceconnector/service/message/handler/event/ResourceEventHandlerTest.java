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
package io.dataspaceconnector.service.message.handler.event;

import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {ResourceEventHandler.class})
class ResourceEventHandlerTest {

    @MockBean
    private RequestedResourceService requestedResourceService;

    @MockBean
    private SubscriberNotificationService subscriberNotificationSvc;

    @Autowired
    private ResourceEventHandler eventHandler;

    @Test
    void handleResourceUpdateEvent_willCallNotifySubscriber() {
        /* ARRANGE */
        final var resource = new ResourceBuilder().build();
        final var requestedResource = createRequestedResource();

        Mockito.doReturn(Optional.of(requestedResource)).when(requestedResourceService).getEntityByRemoteId(eq(resource.getId()));

        /* ACT */
        eventHandler.handleResourceUpdateEvent(resource);

        /* ASSERT */
        Mockito.verify(subscriberNotificationSvc, Mockito.atLeastOnce()).notifyOnUpdate(eq(requestedResource));
    }

    @SneakyThrows
    private RequestedResource createRequestedResource() {
        final var constructor = RequestedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
