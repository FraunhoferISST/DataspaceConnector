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
package io.dataspaceconnector.controller.resource.view.artifact;

import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.controller.resource.relation.ArtifactsToAgreementsController;
import io.dataspaceconnector.controller.resource.relation.ArtifactsToRepresentationsController;
import io.dataspaceconnector.controller.resource.type.ArtifactController;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {
        ArtifactViewAssembler.class
})
public class ArtifactViewAssemblerTest {

    @Autowired
    private ArtifactViewAssembler artifactViewAssembler;

    @SpyBean
    private ArtifactFactory artifactFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = artifactViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = artifactViewAssembler.getSelfLink(artifactId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + artifactId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> artifactViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnArtifactView() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var result = artifactViewAssembler.toModel(artifact);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(artifact.getTitle(), result.getTitle());
        Assertions.assertEquals(artifact.getByteSize(), result.getByteSize());
        Assertions.assertEquals(artifact.getCheckSum(), result.getCheckSum());
        Assertions.assertEquals(artifact.getNumAccessed(), result.getNumAccessed());
        Assertions.assertEquals(artifact.getRemoteId(), result.getRemoteId());
        Assertions.assertEquals(artifact.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(artifact.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(artifact.getAdditional(), result.getAdditional());

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

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

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
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + artifactId;
    }

    @SneakyThrows
    private String getArtifactDataLink(final UUID artifactId) {
        return linkTo(methodOn(ArtifactController.class)
                .getData(artifactId, new ArrayList<>(), new QueryInput())).toString();
    }

    private String getArtifactRepresentationsLink(final UUID artifactId) {
        return WebMvcLinkBuilder.linkTo(methodOn(ArtifactsToRepresentationsController.class)
                .getResource(artifactId, null, null)).toString();
    }

    private String getArtifactAgreementsLink(final UUID artifactId) {
        return linkTo(methodOn(ArtifactsToAgreementsController.class)
                .getResource(artifactId, null, null)).toString();
    }
}
