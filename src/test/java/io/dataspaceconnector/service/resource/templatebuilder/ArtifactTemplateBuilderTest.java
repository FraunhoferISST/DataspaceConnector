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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArtifactTemplateBuilderTest {

    private ArtifactService artifactService = Mockito.mock(ArtifactService.class);

    private ArtifactTemplateBuilder builder = new ArtifactTemplateBuilder(artifactService);

    @Test
    public void build_ArtifactTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(null));
    }

    @Test
    public void build_ArtifactTemplateValid_returnNewArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Some title");
        final var template = new ArtifactTemplate(desc);

        final var artifact = getArtifact(desc);

        Mockito.when(artifactService.create(desc)).thenReturn(artifact);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private Artifact getArtifact(ArtifactDesc desc) {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, desc.getTitle());

        return artifact;
    }
}
