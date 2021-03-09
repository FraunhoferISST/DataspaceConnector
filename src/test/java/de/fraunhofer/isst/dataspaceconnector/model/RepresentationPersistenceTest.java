package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.ArtifactRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.RepresentationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class RepresentationPersistenceTest {

    @Autowired
    private RepresentationRepository representationRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @BeforeEach
    public void init() {
        representationRepository.findAll().forEach(r -> representationRepository.delete(r));
        artifactRepository.findAll().forEach(a -> artifactRepository.delete(a));
    }

    @Transactional
    @Test
    public void createRepresentation_noArtifacts_returnSameRepresentation() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        Representation original = getRepresentation();

        /*ACT*/
        original = representationRepository.save(original);
        Representation persisted = representationRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, representationRepository.findAll().size());
        assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createRepresentation_withArtifact_returnSameRepresentation() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        Artifact artifact = artifactRepository.save(getArtifact());
        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact));

        /*ACT*/
        original = representationRepository.save(original);
        Representation persisted = representationRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, representationRepository.findAll().size());
        assertEquals(original, persisted);
        assertEquals(original.getArtifacts(), persisted.getArtifacts());
    }

    @Transactional
    @Test
    public void updateRepresentation_newTitle_returnUpdatedRepresentation() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        Representation original = representationRepository.save(getRepresentation());
        String newTitle = "new title";

        assertEquals(1, representationRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, representationRepository.findAll().size());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(original.getMediaType(), updated.getMediaType());
        assertEquals(original.getLanguage(), updated.getLanguage());
        assertEquals(original.getArtifacts(), updated.getArtifacts());
    }

    @Transactional
    @Test
    public void updateRepresentation_addArtifact_returnUpdatedRepresentation() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        //IDs not used in equals(), thus artifact1 is equal to artifact2 if no field changed
        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = getArtifact();
        artifact2.setTitle("another artifact title");
        artifact2 = artifactRepository.save(artifact2);

        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact1));

        assertEquals(1, representationRepository.findAll().size());
        assertEquals(1, original.getArtifacts().size());

        /*ACT*/
        original.getArtifacts().add(artifact2);
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, representationRepository.findAll().size());
        assertEquals(2, updated.getArtifacts().size());
                assertTrue(updated.getArtifacts()
                .containsAll(Arrays.asList(artifact1, artifact2)));

        //other attributes should remain unchanged
        assertEquals(original.getMediaType(), updated.getMediaType());
        assertEquals(original.getLanguage(), updated.getLanguage());
        assertEquals(original.getTitle(), updated.getTitle());
    }

    @Transactional
    @Test
    public void updateRepresentation_removeArtifact_returnUpdatedRepresentation() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        //IDs not used in equals(), thus artifact1 is equal to artifact2 if no field changed
        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = getArtifact();
        artifact2.setTitle("another artifact title");
        artifact2 = artifactRepository.save(artifact2);

        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact1, artifact2));

        assertEquals(1, representationRepository.findAll().size());
        assertEquals(2, original.getArtifacts().size());

        /*ACT*/
        original.getArtifacts().remove(artifact1);
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, representationRepository.findAll().size());
        assertEquals(1, updated.getArtifacts().size());
        assertFalse(updated.getArtifacts().contains(artifact1));
        assertTrue(updated.getArtifacts().contains(artifact2));

        //other attributes should remain unchanged
        assertEquals(original.getMediaType(), updated.getMediaType());
        assertEquals(original.getLanguage(), updated.getLanguage());
        assertEquals(original.getTitle(), updated.getTitle());
    }

    @Test
    public void deleteRepresentation_noArtifacts_representationDeleted() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());

        Representation representation = representationRepository.save(getRepresentation());

        assertEquals(1, representationRepository.findAll().size());

        /*ACT*/
        representationRepository.delete(representation);

        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());
    }

    @Test
    public void deleteRepresentation_withArtifacts_representationDeletedAndArtifactsNotAffected() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());
        assertTrue(artifactRepository.findAll().isEmpty());

        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = artifactRepository.save(getArtifact());
        Representation representation = representationRepository
                .save(getRepresentationWithArtifacts(artifact1, artifact2));

        assertEquals(1, representationRepository.findAll().size());
        assertEquals(2, artifactRepository.findAll().size());

        /*ACT*/
        representationRepository.delete(representation);

        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());
        assertEquals(2, artifactRepository.findAll().size());
    }

    @Test
    public void deleteArtifact_artifactReferencedByRepresentation_throwDataIntegrityViolationException() {
        /*ASSERT*/
        assertTrue(representationRepository.findAll().isEmpty());
        assertTrue(artifactRepository.findAll().isEmpty());

        Artifact artifact = artifactRepository.save(getArtifact());
        Representation representation = representationRepository
                .save(getRepresentationWithArtifacts(artifact));

        assertEquals(1, representationRepository.findAll().size());
        assertEquals(1, artifactRepository.findAll().size());

        /*ACT*/
        assertThrows(DataIntegrityViolationException.class, () -> artifactRepository.delete(artifact));
    }

    private Artifact getArtifact() {
        ArtifactImpl artifact = new ArtifactImpl();
        artifact.setTitle("artifact title");
        artifact.setNumAccessed(0L);
        return artifact;
    }

    private Representation getRepresentation() {
        Representation representation = new Representation();
        representation.setTitle("title");
        representation.setLanguage("EN");
        representation.setMediaType("JSON");
        return representation;
    }

    private Representation getRepresentationWithArtifacts(Artifact... artifacts) {
        List<Artifact> artifactList = new ArrayList<>(Arrays.asList(artifacts));

        Representation representation = getRepresentation();
        representation.setArtifacts(artifactList);

        return representation;
    }

}
