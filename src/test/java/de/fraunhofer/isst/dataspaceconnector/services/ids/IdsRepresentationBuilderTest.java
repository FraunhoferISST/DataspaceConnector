package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationFactory;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsRepresentationBuilder;
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

@SpringBootTest(classes = {RepresentationFactory.class, ArtifactFactory.class,
        IdsRepresentationBuilder.class, IdsArtifactBuilder.class})
public class IdsRepresentationBuilderTest {

    @Autowired
    private RepresentationFactory representationFactory;

    @Autowired
    private ArtifactFactory artifactFactory;

    @Autowired
    private IdsRepresentationBuilder idsRepresentationBuilder;

    private final String mediaType = "plain/text";

    private final URI standard = URI.create("http://standard.com");

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsRepresentationBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(IdsUtils.getGregorianOf(representation.getCreationDate()), idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(IdsUtils.getGregorianOf(representation.getModificationDate()), idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());
        assertNull(idsRepresentation.getProperties());

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentationWithAdditional();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(IdsUtils.getGregorianOf(representation.getCreationDate()), idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(IdsUtils.getGregorianOf(representation.getModificationDate()), idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());

        assertNotNull(idsRepresentation.getProperties());
        assertEquals(1, idsRepresentation.getProperties().size());
        assertEquals("value", idsRepresentation.getProperties().get("key"));

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
    }

    @Test
    public void create_maxDepth0_returnRepresentationWithoutArtifacts() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation, 0);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(IdsUtils.getGregorianOf(representation.getCreationDate()), idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(IdsUtils.getGregorianOf(representation.getModificationDate()), idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());
        assertNull(idsRepresentation.getProperties());

        assertNull(idsRepresentation.getInstance());
    }

    @Test
    public void create_maxDepth5_returnCompleteRepresentation() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var idsRepresentation = idsRepresentationBuilder.create(representation, 5);

        /* ASSERT */
        assertTrue(idsRepresentation.getId().isAbsolute());
        assertTrue(idsRepresentation.getId().toString().contains(representation.getId().toString()));

        assertEquals(IdsUtils.getGregorianOf(representation.getCreationDate()), idsRepresentation.getCreated());
        assertEquals(Language.EN, idsRepresentation.getLanguage());
        assertEquals(mediaType, idsRepresentation.getMediaType().getFilenameExtension());
        assertEquals(IdsUtils.getGregorianOf(representation.getModificationDate()), idsRepresentation.getModified());
        assertEquals(standard, idsRepresentation.getRepresentationStandard());
        assertNull(idsRepresentation.getProperties());

        final var artifacts = idsRepresentation.getInstance();
        assertEquals(1, artifacts.size());

        final var artifact = getArtifact();
        final var idsArtifact = (de.fraunhofer.iais.eis.Artifact) artifacts.get(0);
        assertEquals(artifact.getTitle(), idsArtifact.getFileName());
        assertEquals(IdsUtils.getGregorianOf(artifact.getCreationDate()), idsArtifact.getCreationDate());
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
    private Representation getRepresentation() {
        final var representationDesc = new RepresentationDesc();
        representationDesc.setTitle("title");
        representationDesc.setLanguage("EN");
        representationDesc.setMediaType(mediaType);
        representationDesc.setStandard(standard.toString());

        final var representation = representationFactory.create(representationDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(representation, ZonedDateTime.now(ZoneOffset.UTC));

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(representation, date);

        final var artifactsField = Representation.class.getDeclaredField("artifacts");
        artifactsField.setAccessible(true);
        artifactsField.set(representation, Collections.singletonList(getArtifact()));

        return representation;
    }

    @SneakyThrows
    private Representation getRepresentationWithAdditional() {
        final var representation = getRepresentation();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = AbstractEntity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(representation, additional);

        return representation;
    }

}
