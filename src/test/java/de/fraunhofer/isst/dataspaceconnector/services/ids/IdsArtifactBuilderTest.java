package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ArtifactFactory.class, IdsArtifactBuilder.class})
public class IdsArtifactBuilderTest {

    @Autowired
    private ArtifactFactory artifactFactory;

    @Autowired
    private IdsArtifactBuilder idsArtifactBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsArtifactBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifactWithAdditional();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());

        assertNotNull(idsArtifact.getProperties());
        assertEquals(1, idsArtifact.getProperties().size());
        assertEquals("value", idsArtifact.getProperties().get("key"));
    }

    @Test
    public void create_maxDepth0_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact, 0);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
    }

    @Test
    public void create_maxDepth5_returnCompleteArtifact() {
        /* ARRANGE */
        final var artifact = getArtifact();

        /* ACT */
        final var idsArtifact = idsArtifactBuilder.create(artifact, 5);

        /* ASSERT */
        assertTrue(idsArtifact.getId().isAbsolute());
        assertTrue(idsArtifact.getId().toString().contains(artifact.getId().toString()));

        assertEquals(idsArtifact.getFileName(), artifact.getTitle());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
        assertNull(idsArtifact.getProperties());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private Artifact getArtifact() {
        final var artifactDesc = new ArtifactDesc();
        artifactDesc.setTitle("title");
        artifactDesc.setAutomatedDownload(false);
        artifactDesc.setValue("value");
        final var artifact = artifactFactory.create(artifactDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(artifact, date);

        return artifact;
    }

    @SneakyThrows
    private Artifact getArtifactWithAdditional() {
        final var artifact = getArtifact();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = AbstractEntity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(artifact, additional);

        return artifact;
    }

}
