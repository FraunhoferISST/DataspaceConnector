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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.Language;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.representation.RepresentationFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RepresentationFactory.class, ArtifactFactory.class,
        IdsRepresentationBuilder.class, IdsArtifactBuilder.class})
public class IdsRepresentationBuilderTest {

    @Autowired
    private RepresentationFactory representationFactory;

    @Autowired
    private ArtifactFactory artifactFactory;

    @Autowired
    private IdsRepresentationBuilder idsRepresentationBuilder;

    @MockBean
    private SelfLinkHelper selfLinkHelper;

    private final String mediaType = "plain/text";

    private final URI standard = URI.create("http://standard.com");

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void init() {
        final var uri = URI.create("https://" + uuid);
        when(selfLinkHelper.getSelfLink(any(Entity.class))).thenReturn(uri);
    }

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsRepresentationBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getCreationDate()),
                idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getModificationDate()),
                idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());
        assertNull(idsRepresentation.getProperties());

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentationWithAdditional();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getCreationDate()),
                idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getModificationDate()),
                idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());

        assertNotNull(idsRepresentation.getProperties());
        assertEquals(1, idsRepresentation.getProperties().size());
        assertEquals("value", idsRepresentation.getProperties().get("key"));

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
    }

    @Test
    public void create_maxDepth0_returnNull() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation, 0);

        /* ASSERT */
        assertNull(idsRepresentation);
    }

    @Test
    public void create_maxDepth5_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation, 5);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getCreationDate()),
                idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(ToIdsObjectMapper.getGregorianOf(representation.getModificationDate()),
                idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());
        assertNull(idsRepresentation.getProperties());

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private Artifact getArtifact() {
        final var artifactDesc = new ArtifactDesc();
        artifactDesc.setTitle("title");
        artifactDesc.setAutomatedDownload(false);
        artifactDesc.setValue("value");
        final var artifact = artifactFactory.create(artifactDesc);

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, uuid);

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(artifact, date);

        return artifact;
    }

    @SneakyThrows
    private Representation getRepresentation() {
        final var representationDesc = new RepresentationDesc();
        representationDesc.setTitle("title");
        representationDesc.setLanguage("EN");
        representationDesc.setMediaType(mediaType);
        representationDesc.setStandard(standard.toString());

        final var representation = representationFactory.create(representationDesc);

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, uuid);

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(representation, ZonedDateTime.now(ZoneOffset.UTC));

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(representation, date);

        final var artifactsField = Representation.class.getDeclaredField("artifacts");
        artifactsField.setAccessible(true);
        artifactsField.set(representation, Collections.singletonList(getArtifact()));

        return representation;
    }

    @SneakyThrows
    private Representation getRepresentationWithAdditional() {
        final var representation = getRepresentation();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = Entity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(representation, additional);

        return representation;
    }

}
