package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.Util;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateUtilsTest {

    @Test
    public void getRepresentationTemplates_validInput_returnRepresentationList() {
        /* ARRANGE */
        final var requestedArtifact = URI.create("https://requestedArtifact");
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder(requestedArtifact).build(), new ArtifactBuilder().build()))
                .build();
        final var requestedArtifacts = List.of(requestedArtifact);
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");
        final var resource = new ResourceBuilder()
                ._representation_(Util.asList(representation))
                .build();

        /* ACT */
        final var result = TemplateUtils
                .getRepresentationTemplates(resource, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(1, result.size());

    }

    @Test
    public void getRepresentationTemplates_missingRepresentations_returnEmptyList() {
        /* ARRANGE */
        final var resource = new ResourceBuilder().build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getRepresentationTemplates(resource,
                requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getArtifactTemplates_validInputsUnequalId_returnEmptyList() {
        /* ARRANGE */
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder().build()))
                .build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(representation,
                requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getArtifactTemplates_validInputsMatchingId_returnArtifactList() {
        /* ARRANGE */
        final var requestedArtifact = URI.create("https://requestedArtifact");
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder(requestedArtifact).build()))
                .build();
        final var requestedArtifacts = List.of(requestedArtifact);
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(representation,
                requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(1, result.size());
        final var template = result.get(0);
        assertNull(template.getOldRemoteId());
        final var desc = template.getDesc();
        assertEquals(requestedArtifact, desc.getRemoteId());
        assertEquals(remoteUrl, desc.getRemoteAddress());
        assertNull(desc.getTitle());
        assertNull(desc.getAccessUrl());
        assertNull(desc.getUsername());
        assertNull(desc.getPassword());
        assertNull(desc.getValue());
        assertEquals(download, desc.isAutomatedDownload());
        assertEquals(0, desc.getAdditional().size());
    }

    @Test
    public void getArtifactTemplates_missingRepresentationInstance_returnEmptyList() {
        /* ARRANGE */
        final var representation = new RepresentationBuilder().build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(representation,
                requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }
}
