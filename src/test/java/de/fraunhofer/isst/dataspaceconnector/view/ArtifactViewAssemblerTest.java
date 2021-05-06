package de.fraunhofer.isst.dataspaceconnector.view;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {ArtifactViewAssembler.class, ViewAssemblerHelper.class,
        ArtifactFactory.class})
public class ArtifactViewAssemblerTest {

    @Autowired
    private ArtifactViewAssembler artifactViewAssembler;

    @Autowired
    private ArtifactFactory artifactFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = artifactViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = artifactViewAssembler.getSelfLink(artifactId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + artifactId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> artifactViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnArtifactView() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var result = artifactViewAssembler.toModel(artifact);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(artifact.getTitle(), result.getTitle());
        assertEquals(artifact.getByteSize(), result.getByteSize());
        assertEquals(artifact.getCheckSum(), result.getCheckSum());
        assertEquals(artifact.getNumAccessed(), result.getNumAccessed());
        assertEquals(artifact.getRemoteId(), result.getRemoteId());
        assertEquals(artifact.getCreationDate(), result.getCreationDate());
        assertEquals(artifact.getModificationDate(), result.getModificationDate());
        assertEquals(artifact.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getArtifactLink(artifact.getId()), selfLink.get().getHref());

        final var dataLink = result.getLink("data");
        assertTrue(dataLink.isPresent());
        assertNotNull(dataLink.get());
        assertEquals(getArtifactDataLink(artifact.getId()), dataLink.get().getHref());

        final var representationsLink = result.getLink("representations");
        assertTrue(representationsLink.isPresent());
        assertNotNull(representationsLink.get());
        assertEquals(getArtifactRepresentationsLink(artifact.getId()),
                representationsLink.get().getHref());

        final var agreementsLink = result.getLink("agreements");
        assertTrue(agreementsLink.isPresent());
        assertNotNull(agreementsLink.get());
        assertEquals(getArtifactAgreementsLink(artifact.getId()),
                agreementsLink.get().getHref());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Artifact getArtifact() {
        final var desc = new ArtifactDesc();
        desc.setTitle("title");
        desc.setAutomatedDownload(false);
        desc.setValue("value");
        desc.setRemoteId(URI.create("https://remote-id.com"));
        final var artifact = artifactFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(artifact, "creationDate", date);
        ReflectionTestUtils.setField(artifact, "modificationDate", date);
        ReflectionTestUtils.setField(artifact, "additional", additional);

        return artifact;
    }

    private String getArtifactLink(final UUID artifactId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + artifactId;
    }

    private String getArtifactDataLink(final UUID artifactId) {
        return linkTo(methodOn(ResourceControllers.ArtifactController.class)
                .getData(artifactId, new QueryInput())).toString();
    }

    private String getArtifactRepresentationsLink(final UUID artifactId) {
        return linkTo(methodOn(RelationControllers.ArtifactsToRepresentations.class)
                .getResource(artifactId, null, null, null)).toString();
    }

    private String getArtifactAgreementsLink(final UUID artifactId) {
        return linkTo(methodOn(RelationControllers.ArtifactsToAgreements.class)
                .getResource(artifactId, null, null, null)).toString();
    }

}
