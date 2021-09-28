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
package io.dataspaceconnector.service.resource.ids.builder;

import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.base.Entity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ArtifactFactory.class, IdsArtifactBuilder.class})
public class IdsArtifactBuilderTest {

    @Autowired
    private ArtifactFactory artifactFactory;

    @Autowired
    private IdsArtifactBuilder idsArtifactBuilder;

    @MockBean
    private SelfLinkHelper selfLinkHelper;

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
        assertThrows(NullPointerException.class, () -> idsArtifactBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifactWithAdditional();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());

        assertNotNull(idsArtifact.getProperties());
        assertEquals(1, idsArtifact.getProperties().size());
        assertEquals("value", idsArtifact.getProperties().get("key"));
    }

    @Test
    public void create_maxDepth0_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact, 0);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
    }

    @Test
    public void create_maxDepth5_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact, 5);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(ToIdsObjectMapper.getGregorianOf(artifact.getCreationDate()),
                idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
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
    private Artifact getArtifactWithAdditional() {
        final var artifact = getArtifact();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = Entity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(artifact, additional);

        return artifact;
    }

}
