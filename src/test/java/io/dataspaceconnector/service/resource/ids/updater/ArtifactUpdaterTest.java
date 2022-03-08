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
package io.dataspaceconnector.service.resource.ids.updater;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ArtifactUpdater.class})
public class ArtifactUpdaterTest {

    @MockBean
    private ArtifactService artifactService;

    @Autowired
    private ArtifactUpdater updater;

    private final UUID artifactId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    private final Artifact artifact = getArtifact();
    private final io.dataspaceconnector.model.artifact.Artifact dscArtifact = getDscArtifact();
    private final io.dataspaceconnector.model.artifact.Artifact dscUpdatedArtifact = getUpdatedDscArtifact();
    private final ArtifactTemplate template = getTemplate();

    @Test
    public void update_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> updater.update(null));
    }

    @Test
    public void update_entityUnknownRemoteId_throwsResourceNotFoundException() {
        /* ARRANGE */
        Mockito.doReturn(Optional.empty())
                .when(artifactService)
                .identifyByRemoteId(Mockito.eq(artifact.getId()));

        /* ACT && ASSERT */
        final var result = assertThrows(ResourceNotFoundException.class,
                () -> updater.update(artifact));
        assertEquals(artifactId.toString(), result.getMessage());
    }

    @Test
    public void update_knownId_returnUpdatedArtifact() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(artifactId))
                .when(artifactService)
                .identifyByRemoteId(Mockito.eq(artifact.getId()));

        Mockito.doReturn(dscArtifact)
                .when(artifactService)
                .get(Mockito.eq(artifactId));

        Mockito.doReturn(dscUpdatedArtifact)
                .when(artifactService)
                .update(Mockito.eq(artifactId), Mockito.eq(template.getDesc()));

        /* ACT && ASSERT */
        final var result = updater.update(artifact);
        assertEquals(dscUpdatedArtifact, result);
        Mockito.verify(artifactService, Mockito.atLeastOnce()).update(Mockito.eq(artifactId),
                Mockito.eq(template.getDesc()));
    }


    private Artifact getArtifact() {
        return new ArtifactBuilder(URI.create(artifactId.toString()))
                ._fileName_("HELLO").build();
    }

    private io.dataspaceconnector.model.artifact.Artifact getDscArtifact() {
        final var output = new io.dataspaceconnector.model.artifact.ArtifactImpl();
        ReflectionTestUtils.setField(output, "title", "SOME TITLE");
        return output;
    }

    private io.dataspaceconnector.model.artifact.Artifact getUpdatedDscArtifact() {
        final var output = new io.dataspaceconnector.model.artifact.ArtifactImpl();
        ReflectionTestUtils.setField(output, "title", "HELLO");
        return output;
    }

    private ArtifactTemplate getTemplate() {
        final var output = new ArtifactTemplate(new ArtifactDesc());
        output.getDesc().setTitle("HELLO");
        output.getDesc().setRemoteId(URI.create("550e8400-e29b-11d4-a716-446655440000"));
        output.getDesc().setAutomatedDownload(false);
        output.getDesc().setAdditional(new ConcurrentHashMap<>());
        output.getDesc().setBootstrapId(URI.create("550e8400-e29b-11d4-a716-446655440000"));

        return output;
    }
}
