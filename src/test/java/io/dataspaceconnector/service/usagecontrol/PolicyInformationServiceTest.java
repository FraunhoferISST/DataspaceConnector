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
package io.dataspaceconnector.service.usagecontrol;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PolicyInformationService.class})
public class PolicyInformationServiceTest {

    @MockBean
    private ArtifactService artifactService;

    @Autowired
    private PolicyInformationService policyInformationService;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final long numAccessed = 1;

    @Test
    public void getCreationDate_artifactPresent_returnCreationDate() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var targetUri = URI.create("https://localhost:8080/api/artifacts" + artifact.getId());

        when(artifactService.get(artifact.getId())).thenReturn(artifact);

        /* ACT */
        final var result = policyInformationService.getCreationDate(targetUri);

        /* ASSERT */
        assertEquals(date, result);
    }

    @Test
    public void getAccessNumber_artifactPresent_returnNumAccessed() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var targetUri = URI.create("https://localhost:8080/api/artifacts" + artifact.getId());

        when(artifactService.get(artifact.getId())).thenReturn(artifact);

        /* ACT */
        final var result = policyInformationService.getAccessNumber(targetUri);

        /* ASSERT */
        assertEquals(numAccessed, result);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Artifact getArtifact() {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(artifact, "creationDate", date);
        ReflectionTestUtils.setField(artifact, "numAccessed", numAccessed);
        return artifact;
    }
}
