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
package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {AgreementArtifactLinker.class})
class AgreementArtifactLinkerTest {

    @MockBean
    ArtifactService artifactService;

    @MockBean
    AgreementService agreementService;

    @Autowired
    @InjectMocks
    AgreementArtifactLinker linker;

    Artifact artifact = getArtifact();
    Agreement agreement = getAgreement();

    /***********************************************************************************************
     * getInternal                                                                                 *
     **********************************************************************************************/

    @Test
    public void getInternal_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> linker.getInternal(null));
    }

    @Test
    public void getInternal_Valid_returnOfferedResources() {
        /* ARRANGE */
        agreement.getArtifacts().add(artifact);

        /* ACT */
        final var artifacts = linker.getInternal(agreement);

        /* ASSERT */
        final var expected = List.of(artifact);
        assertEquals(expected, artifacts);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private ArtifactImpl getArtifact() {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", "Artifact");
        ReflectionTestUtils.setField(artifact, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private Agreement getAgreement() {
        final var constructor = Agreement.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var agreement = constructor.newInstance();
        ReflectionTestUtils.setField(agreement, "artifacts", new ArrayList<Artifact>());
        ReflectionTestUtils.setField(agreement, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return agreement;
    }
}
