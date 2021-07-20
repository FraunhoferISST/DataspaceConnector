package io.dataspaceconnector.camel.event;

import java.util.Optional;

import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.RequestedResourceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
