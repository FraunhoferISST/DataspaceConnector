package io.dataspaceconnector.service.message.subscription;

import java.util.List;

import io.dataspaceconnector.model.ArtifactDesc;
import io.dataspaceconnector.model.ArtifactFactory;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.RepresentationDesc;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.model.template.ResourceTemplate;
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

    @Test
    public void notifyOnUpdate_emptyOfferedResource_throwNothing() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        final var artifactTemplate = new ArtifactTemplate(desc);

        final var repDesc = new RepresentationDesc();
        final var representationTemplate = new RepresentationTemplate(repDesc);
        representationTemplate.setArtifacts(List.of(artifactTemplate));

        final var offerDesc = new OfferedResourceDesc();
        final var offerTemplate = new ResourceTemplate<>(offerDesc);
        offerTemplate.setRepresentations(List.of(representationTemplate));

        final var offer = tmpBuilder.build(offerTemplate);
        final var artifact = offer.getRepresentations().get(0).getArtifacts().get(0);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> subscriberNotificationSvc.notifyOnUpdate(artifact));
    }

}
