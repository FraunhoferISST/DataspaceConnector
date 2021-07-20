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
package io.dataspaceconnector.service.message.subscription;

import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.service.resource.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.TemplateBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class SubscriberNotificationServiceTest {

    @Autowired
    private SubscriberNotificationService subscriberNotificationSvc;

    @SpyBean
    private TemplateBuilder<OfferedResource, OfferedResourceDesc> tmpBuilder;

    @SpyBean
    private AbstractResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker;

    @SpyBean
    private AbstractResourceContractLinker<OfferedResource> resourceContractLinker;

    @Test
    public void notifyOnUpdate_emptyArtifact_throwNothing() {
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

}
