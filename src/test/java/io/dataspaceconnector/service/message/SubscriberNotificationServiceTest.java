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
package io.dataspaceconnector.service.message;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.resource.templatebuilder.AbstractResourceTemplateBuilder;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SubscriberNotificationServiceTest {

    @Autowired
    private SubscriberNotificationService subscriberNotificationSvc;

    @SpyBean
    private AbstractResourceTemplateBuilder<OfferedResource, OfferedResourceDesc> tmpBuilder;

    @SpyBean
    private AbstractResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker;

    @SpyBean
    private AbstractResourceContractLinker<OfferedResource> resourceContractLinker;

    @MockBean
    private ApiReferenceHelper apiReferenceHelper;

    @MockBean
    private RouteDataDispatcher routeDataDispatcher;

    @MockBean
    private HttpService httpService;

    private final URI target = URI.create("https://artifact");

    @Test
    void notifyOnUpdate_emptyArtifact_throwNothing() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        final var factory = new ArtifactFactory();
        final var artifact = factory.create(desc);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> subscriberNotificationSvc.notifyOnUpdate(artifact));
    }

//    @Test
//    public void notifyOnUpdate_emptyOfferedResource_throwNothing() {
//        /* ARRANGE */
//        final var desc = new ArtifactDesc();
//        final var artifactTemplate = new ArtifactTemplate(desc);
//
//        final var repDesc = new RepresentationDesc();
//        final var representationTemplate = new RepresentationTemplate(repDesc);
//        representationTemplate.setArtifacts(List.of(artifactTemplate));
//
//        final var offerDesc = new OfferedResourceDesc();
//        final var offerTemplate = new ResourceTemplate<>(offerDesc);
//        offerTemplate.setRepresentations(List.of(representationTemplate));
//
//        final var offer = tmpBuilder.build(offerTemplate);
//        final var artifact = offer.getRepresentations().get(0).getArtifacts().get(0);
//
//        /* ACT && ASSERT */
//        assertDoesNotThrow(() -> subscriberNotificationSvc.notifyOnUpdate(artifact));
//    }

    @Test
    @SneakyThrows
    void notifyAll_nonIdsSubscriber_sendNotifications() {
        /* ARRANGE */
        final var nonRouteLocation = URI.create("https://location");
        final var routeLocation = URI.create("https://route");
        final var nonRouteSubscription = getSubscription(nonRouteLocation);
        final var routeSubscription = getSubscription(routeLocation);
        final var subscriptions = List.of(nonRouteSubscription, routeSubscription);
        final var artifact = getArtifact();

        when(apiReferenceHelper.isRouteReference(nonRouteLocation.toURL())).thenReturn(false);
        when(apiReferenceHelper.isRouteReference(routeLocation.toURL())).thenReturn(true);

        /* ACT */
        subscriberNotificationSvc.notifyAll(subscriptions, target, artifact);

        /* ASSERT */
        verify(routeDataDispatcher, times(1)).send(any(), any(), any());
        verify(httpService, times(1)).post(any(), any(), any());
    }

    private Subscription getSubscription(final URI location) {
        final var subscription = new Subscription();
        ReflectionTestUtils.setField(subscription, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(subscription, "target", target);
        ReflectionTestUtils.setField(subscription, "location", location);
        ReflectionTestUtils.setField(subscription, "idsProtocol", false);
        ReflectionTestUtils.setField(subscription, "pushData", false);
        return subscription;
    }

    private ArtifactImpl getArtifact() {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils
                .setField(artifact, "representations", new ArrayList<Representation>());
        return artifact;
    }

}
