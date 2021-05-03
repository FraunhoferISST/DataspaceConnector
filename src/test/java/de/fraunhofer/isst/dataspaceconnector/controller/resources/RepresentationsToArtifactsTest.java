package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationServices;
import de.fraunhofer.isst.dataspaceconnector.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RelationControllers.RepresentationsToArtifacts.class})
class RepresentationsToArtifactsTest {

    @MockBean
    RelationServices.RepresentationArtifactLinker linker;

    @MockBean
    RepresentationModelAssembler<Artifact, ArtifactView> assembler;

    @MockBean
    PagedResourcesAssembler<Artifact> pagedResourcesAssembler;

    @Autowired
    @InjectMocks
    private RelationControllers.RepresentationsToArtifacts controller;

    private Representation representation = getRepresentation("Owner");
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
        assertThrows(IllegalArgumentException.class, () -> controller.getResource(null, 0, null, null));
    }

    @Test
    public void getResource_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final UUID unknownUUid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        Mockito.when(linker.get(Mockito.eq(unknownUUid), Mockito.any())).thenThrow(ResourceNotFoundException.class);

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> controller.getResource(unknownUUid, null, null, null));
    }

    @Test
    public void getResource_knownIdNoChildren_returnEmptyPage() {
        /* ARRANGE */
        final UUID knownUUID = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");
        Mockito.when(linker.get(Mockito.eq(knownUUID), Mockito.any())).thenReturn(Utils.toPage(new ArrayList<>(), Pageable.unpaged()));

        /* ACT */
        final var result = controller.getResource(knownUUID, null, null, null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
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

//    @Test
//    public void addResources_validInput_returnModifiedResource() {
//        /* ARRANGE */
//        final UUID knownUUID = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");
//        final var validIdList = new ArrayList<URI>();
//        validIdList.add(URI.create("https://randompath/363730ec-dcea-45a0-9469-296b868e6a4b"));
//        validIdList.add(URI.create("https://rando/path/acb249b6-7e51-4d50-a162-0bb71ecd9c2c"));
//        validIdList.add(URI.create("https://6c0a6b4e-5713-4264-98c2-adab3a6b8782"));
//
//        Mockito.when(linker.get(knownUUID, Pageable.unpaged())).thenReturn(validIdList);
//
//        /* ACT */
//        final var result = controller.addResources(knownUUID, validIdList);
//
//        /* ASSERT */
//
//    }

    /**
     * Utilities
     */
    @SneakyThrows
    private Representation getRepresentation(final String title) {
        final var constructor = Representation.class.getConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();

        final var titleField = representation.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(representation, title);

        final var artifactsField = representation.getClass().getDeclaredField("artifacts");
        artifactsField.setAccessible(true);
        artifactsField.set(representation, new ArrayList<ArtifactImpl>());

        final var idField = representation.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var additionalField =
                representation.getClass().getSuperclass().getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(representation, new HashMap<>());

        return representation;
    }

    @SneakyThrows
    private ArtifactImpl getArtifact(String title) {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, title);

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.randomUUID());

        return artifact;
    }
}
