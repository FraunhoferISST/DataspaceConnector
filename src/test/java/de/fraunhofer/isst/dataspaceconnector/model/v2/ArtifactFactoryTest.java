package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ArtifactFactoryTest {
    @Autowired
    private ArtifactFactory factory;

    @Test
    void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void create_allDescMembersNull_returnDefaultLocalArtifact() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void create_validDescRemoteData_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescRemoteDataV1();

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void create_validDescLocalData_returnLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescLocalDataV1();

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void create_validDescAllMembersSet_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescRemoteDataV1();

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void create_validDescAllExceptUrlSet_returnLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescAllSetV1();
        desc.setAccessUrl(null);

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
    }

    @Test
    void create_validDescAllExceptUrlAndUsernameSet_returnLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescAllSetV1();
        desc.setAccessUrl(null);
        desc.setUsername(null);

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
    }

    @Test
    void create_validDescAllExceptUrlAndPasswordSet_returnLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescAllSetV1();
        desc.setAccessUrl(null);
        desc.setPassword(null);

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
    }

    @Test
    void create_validDescAllExceptUsernameAndPasswordSet_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescAllSetV1();
        desc.setUsername(null);
        desc.setPassword(null);

        /* ACT */
        final var artifact = factory.create(desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
    }

    @Test
    void update_localDataAllDescMembersNull_returnDefaultLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescLocalDataV1();
        final var artifact = factory.create(desc);

        /* ACT */
        factory.update(artifact, getDescWithNullMembers());

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertNotEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());
        assertNotEquals(((LocalData) artifact.getData()).getValue(), desc.getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_remoteDataAllDescMembersNull_returnDefaultLocalDataArtifact() {
        /* ARRANGE */
        final var desc = getValidDescRemoteDataV1();
        final var artifact = factory.create(desc);
        assertNotNull(artifact);

        /* ACT */
        factory.update(artifact, getDescWithNullMembers());

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertNotEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());
        assertNotEquals(((LocalData) artifact.getData()).getValue(), desc.getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_localDataValidLocalDataDesc_returnLocalDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescLocalDataV2();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());
        assertEquals(((LocalData) artifact.getData()).getValue(), desc.getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_localDataValidRemoteDataDesc_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescRemoteDataV1();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertNotNull(((RemoteData) artifact.getData()).getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertNotNull(((RemoteData) artifact.getData()).getUsername());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertNotNull(((RemoteData) artifact.getData()).getPassword());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_localDataAllDataMembersSet_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescAllSetV1();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertNotNull(((RemoteData) artifact.getData()).getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertNotNull(((RemoteData) artifact.getData()).getUsername());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertNotNull(((RemoteData) artifact.getData()).getPassword());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_remoteDataValidLocalDataDesc_returnLocalDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescRemoteDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescLocalDataV1();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof LocalData);
        assertNotNull(((LocalData) artifact.getData()).getValue());
        assertEquals(((LocalData) artifact.getData()).getValue(), desc.getValue());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_remoteDataValidRemoteDataDesc_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescRemoteDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescRemoteDataV2();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertNotNull(((RemoteData) artifact.getData()).getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertNotNull(((RemoteData) artifact.getData()).getUsername());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertNotNull(((RemoteData) artifact.getData()).getPassword());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_remoteDataAllDataMembersSet_returnRemoteDataArtifact() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescRemoteDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescAllSetV2();

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertNotNull(artifact);
        assertNotNull(artifact.getTitle());
        assertEquals(artifact.getTitle(), desc.getTitle());

        assertNotNull(artifact.getData());
        assertTrue(artifact.getData() instanceof RemoteData);
        assertNotNull(((RemoteData) artifact.getData()).getAccessUrl());
        assertEquals(((RemoteData) artifact.getData()).getAccessUrl(), desc.getAccessUrl());
        assertNotNull(((RemoteData) artifact.getData()).getUsername());
        assertEquals(((RemoteData) artifact.getData()).getUsername(), desc.getUsername());
        assertNotNull(((RemoteData) artifact.getData()).getPassword());
        assertEquals(((RemoteData) artifact.getData()).getPassword(), desc.getPassword());

        assertNull(artifact.getId());
        assertNull(artifact.getCreationDate());
        assertNull(artifact.getLastModificationDate());
    }

    @Test
    void update_localDataChangeValidLocalDesc_true() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescLocalDataV2();

        /* ACT && ASSERT */
        assertTrue(factory.update(artifact, desc));
    }

    @Test
    void update_localDataChangeValidRemoteDesc_true() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescRemoteDataV1();

        /* ACT && ASSERT */
        assertTrue(factory.update(artifact, desc));
    }

    @Test
    void update_localDataSameValidLocalDesc_false() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescLocalDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescLocalDataV1();

        /* ACT && ASSERT */
        assertFalse(factory.update(artifact, desc));
    }

    @Test
    void update_remoteDataSameValidRemoteDesc_false() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescRemoteDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescRemoteDataV1();

        /* ACT && ASSERT */
        assertFalse(factory.update(artifact, desc));
    }

    @Test
    void update_remoteDataSameValidLocalDesc_true() {
        /* ARRANGE */
        final var artifact = factory.create(getValidDescRemoteDataV1());
        assertNotNull(artifact);

        final var desc = getValidDescLocalDataV1();

        /* ACT && ASSERT */
        assertTrue(factory.update(artifact, desc));
    }

    @Test
    void update_nullArtifactValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDescAllSetV1();

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void update_nullArtifactNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void update_validArtifactNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var artifact = factory.create(getValidDescAllSetV1());

        assertNotNull(artifact);

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    ArtifactDesc getDescWithNullMembers() {
        final var desc = new ArtifactDesc();
        desc.setTitle(null);
        desc.setAccessUrl(null);
        desc.setUsername(null);
        desc.setPassword(null);
        desc.setValue(null);

        return desc;
    }

    ArtifactDesc getValidDescRemoteDataV1() {
        final var desc = new ArtifactDesc();
        desc.setTitle("RandomTitle");
        try {
            desc.setAccessUrl(new URL("https://localhost:8080"));
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        desc.setUsername("");
        desc.setPassword("");
        desc.setValue(null);

        return desc;
    }

    ArtifactDesc getValidDescRemoteDataV2() {
        final var desc = new ArtifactDesc();
        desc.setTitle("Well...");
        try {
            desc.setAccessUrl(new URL("https://localhost:9090"));
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        desc.setUsername("");
        desc.setPassword("");
        desc.setValue(null);

        return desc;
    }

    ArtifactDesc getValidDescLocalDataV1() {
        final var desc = new ArtifactDesc();
        desc.setTitle("RandomTitle");
        desc.setAccessUrl(null);
        desc.setUsername(null);
        desc.setPassword(null);
        desc.setValue("Random Value");

        return desc;
    }

    ArtifactDesc getValidDescLocalDataV2() {
        final var desc = new ArtifactDesc();
        desc.setTitle("Something different");
        desc.setAccessUrl(null);
        desc.setUsername(null);
        desc.setPassword(null);
        desc.setValue("Something different");

        return desc;
    }

    ArtifactDesc getValidDescAllSetV1() {
        final var remoteDesc = getValidDescRemoteDataV1();
        final var localDesc = getValidDescLocalDataV1();

        remoteDesc.setValue(localDesc.getTitle());

        return remoteDesc;
    }

    ArtifactDesc getValidDescAllSetV2() {
        final var remoteDesc = getValidDescRemoteDataV2();
        final var localDesc = getValidDescLocalDataV2();

        remoteDesc.setValue(localDesc.getTitle());

        return remoteDesc;
    }
}
