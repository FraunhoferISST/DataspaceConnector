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
package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactView;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RepresentationsToArtifactsController.class})
class RepresentationsToArtifactsTest {

    @MockBean
    RepresentationArtifactLinker linker;

    @MockBean
    RepresentationModelAssembler<Artifact, ArtifactView> assembler;

    @SpyBean
    PagedResourcesAssembler<Artifact> pagedResourcesAssembler;

    @Autowired
    @InjectMocks
    private RepresentationsToArtifactsController controller;

    private List<Artifact> artifacts = new ArrayList<>();

    /**
     * Setup.
     */
    @BeforeEach
    public void init() {
        for (int i = 0; i < 50; i++) artifacts.add(getArtifact(String.valueOf(i)));
    }

    /**
     * getResource.
     */

    @Test
    public void getResource_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        Mockito.when(linker.get(Mockito.isNull(), Mockito.any())).thenThrow(IllegalArgumentException.class);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.getResource(null, 0, null));
    }

    @Test
    public void getResource_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final UUID unknownUUid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        Mockito.when(linker.get(Mockito.eq(unknownUUid), Mockito.any())).thenThrow(ResourceNotFoundException.class);

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> controller.getResource(unknownUUid, null, null));
    }

    @Test
    public void getResource_knownIdNoChildren_returnEmptyPage() {
        /* ARRANGE */
        final UUID knownUUID = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");
        Mockito.when(linker.get(Mockito.eq(knownUUID), Mockito.any())).thenReturn(Utils.toPage(new ArrayList<>(), Pageable.unpaged()));

        /* ACT */
        final var result = controller.getResource(knownUUID, null, null);

        /* ASSERT */
        assertEquals(0, result.getMetadata().getNumber());
        assertEquals(0, result.getMetadata().getSize());
        assertEquals(0, result.getMetadata().getTotalElements());
        assertEquals(1, result.getMetadata().getTotalPages());
    }

    /**
     * addResource.
     */

    @Test
    public void addResources_nullOwnerId_throwIllegalArgumentException() {
        /* ARRANGE */
        Mockito.doThrow(IllegalArgumentException.class).when(linker).add(Mockito.isNull(), Mockito.any());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.addResources(null, new ArrayList<>()));
    }

    @Test
    public void addResources_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        Mockito.doThrow(IllegalArgumentException.class).when(linker).add(Mockito.any(), Mockito.isNull());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.addResources(UUID.randomUUID(), null));
    }

    @Test
    public void addResources_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final UUID unknownUUid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        Mockito.doThrow(ResourceNotFoundException.class).when(linker).add(Mockito.eq(unknownUUid), Mockito.any());

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> controller.addResources(unknownUUid, new ArrayList<>()));
    }

    /**
     * Utilities
     */

    @SneakyThrows
    private ArtifactImpl getArtifact(String title) {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", title);
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());

        return artifact;
    }
}
