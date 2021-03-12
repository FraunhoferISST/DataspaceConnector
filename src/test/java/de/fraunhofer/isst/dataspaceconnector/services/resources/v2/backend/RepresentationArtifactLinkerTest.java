package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RepresentationArtifactLinkerTest {

    @SpyBean
    RepresentationService representationService;

    @SpyBean
    ArtifactService artifactService;

    @Autowired @InjectMocks
    RepresentationArtifactLinker linker;

    /**************************************************************************
     * get
     *************************************************************************/

    @Test
    public void get_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.get(unknownUuid, Pageable.unpaged()));
    }

    @Test
    @Transactional
    public void get_knownId_notNull() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        final var artifactOne = artifactService.create(getArtifactOne());
        final var artifactTwo = artifactService.create(getArtifactTwo());
        final var artifactThree = artifactService.create(getArtifactThree());

        linker.add(representation.getId(), Set.of(artifactOne.getId(), artifactTwo.getId(), artifactThree.getId()));

        /* ACT */
        final var linkedArtifacts = linker.get(representation.getId(), Pageable.unpaged());

        /* ASSERT */
        assertNotNull(linkedArtifacts);
    }

    @Test
    @Transactional
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.get(null, Pageable.unpaged()));
    }

    @Test
    @Transactional
    public void get_nullPageable_throwIllegalArgumentException() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        artifactService.create(getArtifactOne());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.get(representation.getId(), null));
    }

    @Test
    @Transactional
    public void get_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.get(null, null));
    }

    @Test
    @Transactional
    public void get_knownId_getArtifacts() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        final var artifactOne = artifactService.create(getArtifactOne());
        final var artifactTwo = artifactService.create(getArtifactTwo());
        final var artifactThree = artifactService.create(getArtifactThree());

        linker.add(representation.getId(), Set.of(artifactOne.getId(), artifactTwo.getId(), artifactThree.getId()));

        /* ACT */
        final var linkedArtifacts = linker.get(representation.getId(), Pageable.unpaged()).toList();

        /* ASSERT */
        assertTrue(linkedArtifacts.contains(artifactOne));
        assertTrue(linkedArtifacts.contains(artifactTwo));
        assertTrue(linkedArtifacts.contains(artifactThree));
    }

    /**************************************************************************
     * add
     *************************************************************************/

    @Test
    @Transactional
    public void add_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var artifactOne = artifactService.create(getArtifactOne());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(null, Set.of(artifactOne.getId())));
    }

    @Test
    @Transactional
    public void add_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(knownId, null));
    }

    @Test
    @Transactional
    public void add_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(unknownUuid, null));
    }

    @Test
    @Transactional
    public void add_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.add(null, null));
    }

    @Test
    @Transactional
    public void add_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.add(unknownUuid, Set.of(artifactOne)));
    }

    @Test
    @Transactional
    public void add_knownIdEmptyEntities_noOwnerServiceActions() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();

        Mockito.reset(representationService);

        /* ACT */
        linker.add(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    @Transactional
    public void add_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
         assertThrows(ResourceNotFoundException.class, () -> linker.add(knownId, Set.of(artifactOne, unknownUuid)));
    }

    @Test
    @Transactional
    public void add_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        try {
            /* ACT */
            linker.add(knownId, Set.of(artifactOne, unknownUuid));
        } catch(ResourceNotFoundException exception) {
            /* ASSERT */
            assertEquals(linker.get(knownId, Pageable.unpaged()).getTotalElements(), 0);
        }


    }

    @Test
    @Transactional
    public void add_knownEntities_createRelation() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactOne));
        assertTrue(elements.contains(artifactTwo));
        assertTrue(elements.contains(artifactThree));
    }

    @Test
    @Transactional
    public void add_knownEntities_createRelationOneOfEach() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before, after + 3);
    }

    @Test
    @Transactional
    public void add_knownEntitiesAlreadyExists_createOnlyMissingRelations() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne, artifactThree));

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before, after + 1);
    }

    @Test
    @Transactional
    public void add_validInput_isPersisted() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne));

        /* ASSERT */
        Mockito.verify(representationService, Mockito.atLeastOnce()).persist(Mockito.eq(representation));
    }

    /**************************************************************************
     * remove
     *************************************************************************/

    @Test
    @Transactional
    public void remove_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var artifactOne = artifactService.create(getArtifactOne());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(null, Set.of(artifactOne.getId())));
    }

    @Test
    @Transactional
    public void remove_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(knownId, null));
    }

    @Test
    @Transactional
    public void remove_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(unknownUuid, null));
    }

    @Test
    @Transactional
    public void remove_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.remove(null, null));
    }

    @Test
    @Transactional
    public void remove_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.remove(unknownUuid, Set.of(artifactOne)));
    }

    @Test
    @Transactional
    public void remove_knownIdEmptyEntities_noOwnerServiceActions() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();

        Mockito.reset(representationService);

        /* ACT */
        linker.remove(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    @Transactional
    public void remove_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        linker.add(knownId, Set.of(artifactOne));

        /* ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.remove(knownId, Set.of(artifactOne, unknownUuid)));
    }

    @Test
    @Transactional
    public void remove_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne));

        try {
            /* ACT */
            linker.remove(knownId, Set.of(artifactOne, unknownUuid));
        }catch(ResourceNotFoundException exception) {
            /* ASSERT */
            assertEquals(linker.get(knownId, Pageable.unpaged()).getTotalElements(), 1);
        }
    }

    @Test
    @Transactional
    public void remove_knownEntities_removeRelation() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ACT */
        linker.remove(knownId, Set.of(artifactOne, artifactTwo));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactThree));
    }

    @Test
    @Transactional
    public void remove_validInput_isPersisted() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        linker.add(knownId, Set.of(artifactOne));

        Mockito.reset(representationService);

        /* ACT */
        linker.remove(knownId, Set.of(artifactOne));

        /* ASSERT */
        Mockito.verify(representationService, Mockito.atLeastOnce()).persist(Mockito.eq(representation));
    }

    /**************************************************************************
     * replace
     *************************************************************************/

    @Test
    @Transactional
    public void replace_nullId_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var artifactOne = artifactService.create(getArtifactOne());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(null, Set.of(artifactOne.getId())));
    }

    @Test
    @Transactional
    public void replace_knownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(knownId, null));
    }

    @Test
    @Transactional
    public void replace_unknownIdNullEntities_throwsIllegalArgumentsException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(unknownUuid, null));
    }

    @Test
    @Transactional
    public void replace_null_throwsIllegalArgumentsException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> linker.replace(null, null));
    }

    @Test
    @Transactional
    public void replace_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.remove(unknownUuid, Set.of(artifactOne)));
    }

    @Test
    @Transactional
    public void replace_knownIdEmptySet_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of());

        /* ASSERT */
        final var numElements = linker.get(knownId, Pageable.unpaged()).getTotalElements();
        assertEquals(numElements, 0);
    }

    @Test
    @Transactional
    public void replace_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo, unknownUuid));

        /* ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.remove(knownId, Set.of(artifactOne, unknownUuid)));
    }

    @Test
    @Transactional
    public void replace_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne));

        try {
            /* ACT */
            linker.replace(knownId, Set.of(artifactTwo, unknownUuid));
        }catch(ResourceNotFoundException exception) {
            /* ASSERT */
            final var entities = linker.get(knownId, Pageable.unpaged()).toList();
            assertTrue(entities.contains(artifactOne));
            assertEquals(entities.size(), 1);
        }
    }

    @Test
    @Transactional
    public void replace_addKnownEntities_removeRelation() {
        /* ARRANGE */
        final var knownId = representationService.create(getRepresentation()).getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo, artifactThree));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(artifactTwo));
        assertTrue(elements.contains(artifactThree));
        assertFalse(elements.contains(artifactOne));
    }

    @Test
    @Transactional
    public void replace_validInput_isPersisted() {
        /* ARRANGE */
        final var representation = representationService.create(getRepresentation());
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne));

        Mockito.reset(representationService);

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo, artifactThree));

        /* ASSERT */
        Mockito.verify(representationService, Mockito.atLeastOnce()).persist(Mockito.eq(representation));
    }

    /**************************************************************************
     * Utilities
     *************************************************************************/

    private RepresentationDesc getRepresentation() {
        final var desc = new RepresentationDesc();
        desc.setTitle("HELLO");
        desc.setStaticId(UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));
        desc.setLanguage("en");

        return desc;
    }

    private ArtifactDesc getArtifactOne() {
        final var desc = new ArtifactDesc();
        desc.setTitle("ARTIFACT1");
        desc.setValue("VALUE");

        return desc;
    }

    private ArtifactDesc getArtifactTwo() {
        final var desc = new ArtifactDesc();
        desc.setTitle("ARTIFACT2");
        desc.setValue("VALUE");

        return desc;
    }

    private ArtifactDesc getArtifactThree() {
        final var desc = new ArtifactDesc();
        desc.setTitle("ARTIFACT3");
        desc.setValue("VALUE");

        return desc;
    }
}
