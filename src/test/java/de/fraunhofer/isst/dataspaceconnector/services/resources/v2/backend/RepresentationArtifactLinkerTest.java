package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RepresentationArtifactLinkerTest {

    @MockBean
    RepresentationService representationService;

    @SpyBean
    ArtifactService artifactService;

    @Autowired @InjectMocks
    RepresentationArtifactLinker linker;

    Representation representation = getRepresentation();

    @BeforeEach
    public void init() {
        Mockito.when(representationService.get(Mockito.eq(representation.getId()))).thenReturn(representation);
        Mockito.when(representationService.get(AdditionalMatchers.not(AdditionalMatchers.and(Mockito.notNull(), Mockito.eq(representation.getId()))))).thenThrow(ResourceNotFoundException.class);
        Mockito.when(representationService.persist(Mockito.any())).thenAnswer(
                (Answer<Representation>) invocationOnMock -> invocationOnMock.getArgument(0));
    }

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
        final var knownId = representation.getId();

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
        final var knownId = representation.getId();

        /* ACT */
        linker.add(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    @Transactional
    public void add_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
         assertThrows(ResourceNotFoundException.class, () -> linker.add(knownId, Set.of(artifactOne, unknownUuid)));
    }

    @Test
    @Transactional
    public void add_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representation.getId();
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
        final var knownId = representation.getId();
        final var realArtifactOne = artifactService.create(getArtifactOne());
        final var realArtifactTwo = artifactService.create(getArtifactTwo());
        final var realArtifactThree = artifactService.create(getArtifactThree());
        final var artifactOne = realArtifactOne.getId();
        final var artifactTwo = realArtifactTwo.getId();
        final var artifactThree = realArtifactThree.getId();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(realArtifactOne));
        assertTrue(elements.contains(realArtifactTwo));
        assertTrue(elements.contains(realArtifactThree));
    }

    @Test
    @Transactional
    public void add_knownEntities_createRelationOneOfEach() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before + 3, after );
    }

    @Test
    @Transactional
    public void add_knownEntitiesAlreadyExists_createOnlyMissingRelations() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne, artifactThree));

        final var before = linker.get(knownId, Pageable.unpaged()).toList().size();

        /* ACT */
        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ASSERT */
        final var after = linker.get(knownId, Pageable.unpaged()).toList().size();
        assertEquals(before + 1, after);
    }

    @Test
    @Transactional
    public void add_validInput_isPersisted() {
        /* ARRANGE */
        
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
        final var knownId = representation.getId();

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
        final var knownId = representation.getId();

        /* ACT */
        linker.remove(knownId, Set.of());

        /* ASSERT */
        Mockito.verifyNoInteractions(representationService, artifactService);
    }

    @Test
    @Transactional
    public void remove_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
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
        final var knownId = representation.getId();
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
        final var knownId = representation.getId();
        final var realArtifactThree = artifactService.create(getArtifactThree());
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = realArtifactThree.getId();

        linker.add(knownId, Set.of(artifactOne, artifactTwo, artifactThree));

        /* ACT */
        linker.remove(knownId, Set.of(artifactOne, artifactTwo));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(realArtifactThree));
    }

    @Test
    @Transactional
    public void remove_validInput_isPersisted() {
        /* ARRANGE */
        
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        linker.add(knownId, Set.of(artifactOne));

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
        final var knownId = representation.getId();

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
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of());

        /* ASSERT */
        final var numElements = linker.get(knownId, Pageable.unpaged()).getTotalElements();
        assertEquals(0, numElements);
    }

    @Test
    @Transactional
    public void replace_knownIdSetWithUnknownEntities_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne));

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> linker.replace(knownId, Set.of(artifactTwo, unknownUuid)));
    }

    @Test
    @Transactional
    public void replace_knownIdSetWithUnknownEntities_doNotModify() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var realArtifactOne = artifactService.create(getArtifactOne());
        final var artifactOne = realArtifactOne.getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        linker.add(knownId, Set.of(artifactOne));

        try {
            /* ACT */
            linker.replace(knownId, Set.of(artifactTwo, unknownUuid));
        }catch(ResourceNotFoundException exception) {
            /* ASSERT */
            final var entities = linker.get(knownId, Pageable.unpaged()).toList();
            assertTrue(entities.contains(realArtifactOne));
            assertEquals(entities.size(), 1);
        }
    }

    @Test
    @Transactional
    public void replace_addKnownEntities_removeRelation() {
        /* ARRANGE */
        final var knownId = representation.getId();
        final var realArtifactOne = artifactService.create(getArtifactOne());
        final var realArtifactTwo = artifactService.create(getArtifactTwo());
        final var realArtifactThree = artifactService.create(getArtifactThree());
        final var artifactOne = realArtifactOne.getId();
        final var artifactTwo = realArtifactTwo.getId();
        final var artifactThree = realArtifactThree.getId();

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo, artifactThree));

        /* ASSERT */
        final var elements = linker.get(knownId, Pageable.unpaged()).toList();
        assertTrue(elements.contains(realArtifactTwo));
        assertTrue(elements.contains(realArtifactThree));
        assertFalse(elements.contains(realArtifactOne));
    }

    @Test
    @Transactional
    public void replace_validInput_isPersisted() {
        /* ARRANGE */
        
        final var knownId = representation.getId();
        final var artifactOne = artifactService.create(getArtifactOne()).getId();
        final var artifactTwo = artifactService.create(getArtifactTwo()).getId();
        final var artifactThree = artifactService.create(getArtifactThree()).getId();

        linker.add(knownId, Set.of(artifactOne));

        /* ACT */
        linker.replace(knownId, Set.of(artifactTwo, artifactThree));

        /* ASSERT */
        Mockito.verify(representationService, Mockito.atLeastOnce()).persist(Mockito.eq(representation));
    }

    @Test
    public void test() {
        getRepresentation();
    }

    /**************************************************************************
     * Utilities
     *************************************************************************/

    @SneakyThrows
    private Representation getRepresentation() {
        final var constructor = Representation.class.getConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();

        final var titleField = representation.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(representation, "Hello");

        final var mediaTypeField = representation.getClass().getDeclaredField("mediaType");
        mediaTypeField.setAccessible(true);
        mediaTypeField.set(representation, "application/json");

        final var languageField = representation.getClass().getDeclaredField("language");
        languageField.setAccessible(true);
        languageField.set(representation, "en");

        final var artifactsField = representation.getClass().getDeclaredField("artifacts");
        artifactsField.setAccessible(true);
        artifactsField.set(representation, new ArrayList<ArtifactImpl>());

        final var idField = representation.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var creationDateField = representation.getClass().getSuperclass().getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(representation, new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("14-Feb-2021 12:13:14"));

        final var modificationDateField = representation.getClass().getSuperclass().getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(representation, new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("14-Feb-2021 12:13:14"));

        final var additionalField = representation.getClass().getSuperclass().getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(representation, new HashMap<>());

        return representation;
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
