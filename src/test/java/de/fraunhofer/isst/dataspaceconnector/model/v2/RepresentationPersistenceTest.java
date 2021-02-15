package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.ArtifactRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.RepresentationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class RepresentationPersistenceTest {

    @Autowired
    private RepresentationRepository representationRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Before
    public void init() {
        representationRepository.findAll().forEach(r -> representationRepository.delete(r));
        artifactRepository.findAll().forEach(a -> artifactRepository.delete(a));
    }

    @Transactional
    @Test
    public void createRepresentation_noArtifacts_returnSameRepresentation() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Representation original = getRepresentation();

        /*ACT*/
        original = representationRepository.save(original);
        Representation persisted = representationRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createRepresentation_withArtifact_returnSameRepresentation() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Artifact artifact = artifactRepository.save(getArtifact());
        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact));

        /*ACT*/
        original = representationRepository.save(original);
        Representation persisted = representationRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(original, persisted);
        Assert.assertEquals(original.getArtifacts(), persisted.getArtifacts());
    }

    @Transactional
    @Test
    public void updateRepresentation_newTitle_returnUpdatedRepresentation() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Representation original = representationRepository.save(getRepresentation());
        String newTitle = "new title";

        Assert.assertEquals(1, representationRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(newTitle, updated.getTitle());
        Assert.assertEquals(original.getMediaType(), updated.getMediaType());
        Assert.assertEquals(original.getLanguage(), updated.getLanguage());
        Assert.assertEquals(original.getArtifacts(), updated.getArtifacts());
    }

    @Transactional
    @Test
    public void updateRepresentation_addArtifact_returnUpdatedRepresentation() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = artifactRepository.save(getArtifact());
        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact1));

        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(1, original.getArtifacts().size());

        /*ACT*/
        original.getArtifacts().put(artifact2.getId(), artifact2);
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(2, updated.getArtifacts().size());
                Assert.assertTrue(updated.getArtifacts().keySet()
                .containsAll(Arrays.asList(artifact1.getId(), artifact2.getId())));

        //other attributes should remain unchanged
        Assert.assertEquals(original.getMediaType(), updated.getMediaType());
        Assert.assertEquals(original.getLanguage(), updated.getLanguage());
        Assert.assertEquals(original.getTitle(), updated.getTitle());
    }

    @Transactional
    @Test
    public void updateRepresentation_removeArtifact_returnUpdatedRepresentation() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = artifactRepository.save(getArtifact());
        Representation original = representationRepository
                .save(getRepresentationWithArtifacts(artifact1, artifact2));

        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(2, original.getArtifacts().size());

        /*ACT*/
        original.getArtifacts().remove(artifact1.getId());
        representationRepository.save(original);
        Representation updated = representationRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(1, updated.getArtifacts().size());
        Assert.assertFalse(updated.getArtifacts().containsKey(artifact1.getId()));
        Assert.assertTrue(updated.getArtifacts().containsKey(artifact2.getId()));

        //other attributes should remain unchanged
        Assert.assertEquals(original.getMediaType(), updated.getMediaType());
        Assert.assertEquals(original.getLanguage(), updated.getLanguage());
        Assert.assertEquals(original.getTitle(), updated.getTitle());
    }

    @Test
    public void deleteRepresentation_noArtifacts_representationDeleted() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());

        Representation representation = representationRepository.save(getRepresentation());

        Assert.assertEquals(1, representationRepository.findAll().size());

        /*ACT*/
        representationRepository.delete(representation);

        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());
    }

    @Test
    public void deleteRepresentation_withArtifacts_representationDeleted() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());
        Assert.assertTrue(artifactRepository.findAll().isEmpty());

        Artifact artifact1 = artifactRepository.save(getArtifact());
        Artifact artifact2 = artifactRepository.save(getArtifact());
        Representation representation = representationRepository
                .save(getRepresentationWithArtifacts(artifact1, artifact2));

        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(2, artifactRepository.findAll().size());

        /*ACT*/
        representationRepository.delete(representation);

        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());
        Assert.assertEquals(2, artifactRepository.findAll().size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void deleteArtifact_artifactReferencedByRepresentation_throwDataIntegrityViolationException() {
        /*ASSERT*/
        Assert.assertTrue(representationRepository.findAll().isEmpty());
        Assert.assertTrue(artifactRepository.findAll().isEmpty());

        Artifact artifact = artifactRepository.save(getArtifact());
        Representation representation = representationRepository
                .save(getRepresentationWithArtifacts(artifact));

        Assert.assertEquals(1, representationRepository.findAll().size());
        Assert.assertEquals(1, artifactRepository.findAll().size());

        /*ACT*/
        artifactRepository.delete(artifact);
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
        Map<UUID, Artifact> artifactMap = new HashMap<>();
        Arrays.stream(artifacts).forEach(a -> artifactMap.put(a.getId(), a));

        Representation representation = getRepresentation();
        representation.setArtifacts(artifactMap);

        return representation;
    }

}
