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

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {RepresentationArtifactLinker.class})
public class RepresentationArtifactLinkerTest {
    @MockBean
    RepresentationService representationService;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    @InjectMocks
    RepresentationArtifactLinker linker;

    Representation representation = getRepresentation();
    Artifact artifactOne = getArtifactOne();
    Artifact artifactTwo = getArtifactTwo();
    Artifact artifactThree = getArtifactThree();

    @BeforeEach
    public void init() {
        Mockito.when(representationService.get(Mockito.eq(representation.getId())))
                .thenReturn(representation);
        Mockito.when(representationService.get(AdditionalMatchers.not(AdditionalMatchers.and(
                Mockito.notNull(), Mockito.eq(representation.getId())))))
                .thenThrow(ResourceNotFoundException.class);
//        Mockito.when(representationService.persist(Mockito.any()))
//                .thenAnswer((Answer<Representation>) invocationOnMock -> invocationOnMock.getArgument(0));

        Mockito.when(artifactService.get(Mockito.eq(artifactOne.getId()))).thenReturn(artifactOne);
        Mockito.when(artifactService.get(Mockito.eq(artifactTwo.getId()))).thenReturn(artifactTwo);
        Mockito.when(artifactService.get(Mockito.eq(artifactThree.getId())))
                .thenReturn(artifactThree);

        Mockito.when(artifactService.doesExist(Mockito.eq(artifactOne.getId()))).thenReturn(true);
        Mockito.when(artifactService.doesExist(Mockito.eq(artifactTwo.getId()))).thenReturn(true);
        Mockito.when(artifactService.doesExist(Mockito.eq(artifactThree.getId()))).thenReturn(true);
    }

    /***********************************************************************************************
     * get                                                                                         *
     **********************************************************************************************/

    @Test
    public void get_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.get(unknownUuid, Pageable.unpaged()));
    }

    @Test
    public void get_knownId_notNull() {
        /* ARRANGE */
        linker.add(representation.getId(), Set.of(artifactOne.getId(), artifactTwo.getId(),
                artifactThree.getId()));

        /* ACT */
        final var linkedArtifacts = linker.get(representation.getId(), Pageable.unpaged());

        /* ASSERT */
        assertNotNull(linkedArtifacts);
    }

    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.get(null, Pageable.unpaged()));
    }

    @Test
    public void get_nullPageable_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> linker.get(representation.getId(), null));
    }

    @Test
    public void get_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.get(null, null));
    }

    @Test
    public void get_knownId_getArtifacts() {
        /* ARRANGE */
        linker.add(representation.getId(), Set.of(artifactOne.getId(), artifactTwo.getId(),
                artifactThree.getId()));

        /* ACT */
        final var linkedArtifacts = linker.get(representation.getId(), Pageable.unpaged()).toList();

        /* ASSERT */
        assertTrue(linkedArtifacts.contains(artifactOne));
        assertTrue(linkedArtifacts.contains(artifactTwo));
        assertTrue(linkedArtifacts.contains(artifactThree));
    }

    /***********************************************************************************************
     * add                                                                                         *
     **********************************************************************************************/

    @Test
    public void add_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> linker.add(null, Set.of(artifactOne.getId())));
    }

    @Test
    public void add_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(knownId, null));
    }

    @Test
    public void add_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(unknownUuid, null));
    }

    @Test

    public void add_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(null, null));
    }

    @Test
    public void add_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.add(unknownUuid, Set.of(artifactOne.getId())));
    }

    @Test
    public void add_knownIdEmptyEntities_noOwnerServiceActions() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT */
        linker.add(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    public void add_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.add(knownId, Set.of(artifactOne.getId(), unknownUuid)));
    }

    @Test
    public void add_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        try {
            /* ACT */
            linker.add(knownId, Set.of(artifactOne.getId(), unknownUuid));
        } catch (ResourceNotFoundException exception) {
            /* ASSERT */
            assertEquals(0, linker.get(knownId, Pageable.unpaged()).getTotalElements());
        }
    }

    @Test
    public void add_knownIdSetWithNullAndValidEntities_throwsNullPointerException() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class,
                () -> linker.add(knownId, Set.of(artifactOne.getId(), null)));
    }

    @Test
    public void add_knownEntities_createRelation() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne.getId(), artifactTwo.getId(),
                artifactThree.getId()));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactOne));
        assertTrue(elements.contains(artifactTwo));
        assertTrue(elements.contains(artifactThree));
    }

    @Test
    public void add_knownEntities_createRelationOneOfEach() {
        /* ARRANGE */
        final var knownId = representation.getId();

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne.getId(), artifactTwo.getId(),
                artifactThree.getId()));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before + 3, after);
    }

    @Test
    public void add_knownEntitiesAlreadyExists_createOnlyMissingRelations() {
        /* ARRANGE */
        final var knownId = representation.getId();

        linker.add(knownId, Set.of(artifactOne.getId(), artifactThree.getId()));

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne.getId(), artifactTwo.getId(),
                artifactThree.getId()));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before + 1, after);
    }

//    @Test
//    public void add_validInput_isPersisted() {
//        /* ARRANGE */
//        final var knownId = representation.getId();
//
//        /* ACT */
//        linker.add(knownId, Set.of(artifactOne.getId()));
//
//        /* ASSERT */
//        Mockito.verify(representationService, Mockito.atLeast(1))
//                .persist(Mockito.eq(representation));
//    }

    /***********************************************************************************************
     * remove                                                                                      *
     **********************************************************************************************/

    @Test
    public void remove_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> linker.remove(null, Set.of(artifactOne.getId())));
    }

    @Test
    public void remove_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(knownId, null));
    }

    @Test
    public void remove_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(unknownUuid, null));
    }

    @Test
    public void remove_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(null, null));
    }

    @Test
    public void remove_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.remove(unknownUuid, Set.of(artifactOne.getId())));
    }

    @Test
    public void remove_knownIdEmptyEntities_noOwnerServiceActions() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT */
        linker.remove(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    public void remove_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.remove(knownId, Set.of(artifactOne.getId(), unknownUuid)));
    }

    @Test
    public void remove_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne.getId()));

        try {
            /* ACT */
            linker.remove(knownId, Set.of(artifactOne.getId(), unknownUuid));
        } catch (ResourceNotFoundException exception) {
            /* ASSERT */
            assertEquals(1, linker.get(knownId, Pageable.unpaged()).getTotalElements());
        }
    }

    @Test
    public void remove_knownEntities_removeRelation() {
        /* ARRANGE */
        final var knownId = representation.getId();

        linker.add(
                knownId, Set.of(artifactOne.getId(), artifactTwo.getId(), artifactThree.getId()));

        /* ACT */
        linker.remove(knownId, Set.of(artifactOne.getId(), artifactTwo.getId()));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactThree));
        assertEquals(1, elements.size());
    }

//    @Test
//    public void remove_validInput_isPersisted() {
//        /* ARRANGE */
//        final var knownId = representation.getId();
//        linker.add(knownId, Set.of(artifactOne.getId()));
//
//        Mockito.verify(representationService, Mockito.times(1)).persist(Mockito.eq(representation));
//
//        /* ACT */
//        linker.remove(knownId, Set.of(artifactOne.getId()));
//
//        /* ASSERT */
//        Mockito.verify(representationService, Mockito.atLeast(2))
//                .persist(Mockito.eq(representation));
//    }

    @Test
    public void remove_knownIdSetWithNullAndValidEntities_throwsNullPointerException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class,
                () -> linker.remove(knownId, Set.of(artifactOne.getId(), null)));
    }

    /***********************************************************************************************
     * replace                                                                                     *
     **********************************************************************************************/

    @Test
    public void replace_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> linker.replace(null, Set.of(artifactOne.getId())));
    }

    @Test
    public void replace_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representation.getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(knownId, null));
    }

    @Test
    public void replace_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(unknownUuid, null));
    }

    @Test
    public void replace_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(null, null));
    }

    @Test
    public void replace_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.remove(unknownUuid, Set.of(artifactOne.getId())));
    }

    @Test
    public void replace_knownIdEmptySet_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();

        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ACT */
        linker.replace(knownId, Set.of());

        /* ASSERT */
        final var numElements = linker.get(knownId, Pageable.unpaged()).getTotalElements();
        assertEquals(0, numElements);
    }

    @Test
    public void replace_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> linker.replace(knownId, Set.of(artifactTwo.getId(), unknownUuid)));
    }

    @Test
    public void replace_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne.getId()));

        try {
            /* ACT */
            linker.replace(knownId, Set.of(artifactTwo.getId(), unknownUuid));
        } catch (ResourceNotFoundException exception) {
            /* ASSERT */
            final var entities = linker.get(knownId, Pageable.unpaged()).toList();
            assertTrue(entities.contains(artifactOne));
            assertEquals(1, entities.size());
        }
    }

    @Test
    public void replace_addKnownEntities_removeRelation() {
        /* ARRANGE */
        final var knownId = representation.getId();

        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo.getId(), artifactThree.getId()));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactTwo));
        assertTrue(elements.contains(artifactThree));
        assertFalse(elements.contains(artifactOne));
    }

//    @Test
//    public void replace_validInput_isPersisted() {
//        /* ARRANGE */
//        final var knownId = representation.getId();
//
//        linker.add(knownId, Set.of(artifactOne.getId()));
//
//        Mockito.verify(representationService, Mockito.times(1)).persist(Mockito.eq(representation));
//
//        /* ACT */
//        linker.replace(knownId, Set.of(artifactTwo.getId(), artifactThree.getId()));
//
//        /* ASSERT */
//        Mockito.verify(representationService, Mockito.atLeast(2))
//                .persist(Mockito.eq(representation));
//    }

    @Test
    public void replace_knownIdSetWithNullAndValidEntities_throwsNullPointerException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        linker.add(knownId, Set.of(artifactOne.getId()));

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class,
                () -> linker.replace(knownId, Set.of(artifactOne.getId(), null)));
    }

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
    public void getInternal_Valid_returnArtifacts() {
        /* ARRANGE */
        representation.getArtifacts().add(artifactOne);

        /* ACT */
        final var artifacts = linker.getInternal(representation);

        /* ASSERT */
        final var expected = List.of(artifactOne);
        assertEquals(expected, artifacts);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private Representation getRepresentation() {
        final var constructor = Representation.class.getConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();
        ReflectionTestUtils.setField(representation, "title", "Hello");
        ReflectionTestUtils.setField(representation, "mediaType", "application/json");
        ReflectionTestUtils.setField(representation, "language", "en");
        ReflectionTestUtils.setField(representation, "artifacts", new ArrayList<ArtifactImpl>());
        ReflectionTestUtils.setField(representation, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));
        ReflectionTestUtils.setField(representation, "creationDate", ZonedDateTime.parse("2021-02-14T12:13:14+01:00"));
        ReflectionTestUtils.setField(representation, "modificationDate", ZonedDateTime.parse("2021-02-14T12:13:14+01:00"));
        ReflectionTestUtils.setField(representation, "additional", new HashMap<>());

        return representation;
    }

    @SneakyThrows
    private ArtifactImpl getArtifactOne() {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", "ArtifactOne");
        ReflectionTestUtils.setField(artifact, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private ArtifactImpl getArtifactTwo() {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", "ArtifactTwo");
        ReflectionTestUtils.setField(artifact, "id", UUID.fromString("1d853fc2-91a8-4a01-9e59-dfb742eee849"));

        return artifact;
    }

    @SneakyThrows
    private ArtifactImpl getArtifactThree() {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", "ArtifactThree");
        ReflectionTestUtils.setField(artifact, "id", UUID.fromString("afb43170-b8d4-4872-b923-3490de99a53b"));

        return artifact;
    }
}
