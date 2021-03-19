package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.Data;
import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ArtifactServiceTest {

    @SpyBean
    private DataRepository dataRepository;

    @SpyBean
    private HttpService httpService;

    @Autowired
    private ArtifactService service;

    Artifact localArtifact = getLocalArtifact();

    @BeforeEach
    public void init() {
        
    }

    /**************************************************************************
     * getData.
     *************************************************************************/

    @Test
    public void getData_nullArtifactId_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getData(null, queryInput));
    }

    @Test
    public void getData_unknownArtifactIdNullQuery_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.getData(unknownUuid, null));
    }


    @Test
    public void getData_knownArtifactIdNullQuery_returnData() {
        /* ARRANGE */
        final var artifact = service.create(getLocalArtifactDesc());

        /* ACT */
        final var data = service.getData(artifact.getId(), null);

        /* ASSERT */
        assertEquals(getLocalData().getValue(), (String) data);
    }

    @Test
    public void getData_knownArtifactIdNullQuery_increaseAccessCounter() {
        /* ARRANGE */
        final var artifact = service.create(getLocalArtifactDesc());
        final var before = artifact.getNumAccessed();

        /* ACT */
        service.getData(artifact.getId(), null);

        /* ASSERT */
        final var after = artifact.getNumAccessed();
        assertEquals(before + 1, after);
    }

    @Test
    public void getData_knownArtifactIdNullQuery_accessCounterPersisted() {
        /* ARRANGE */
        final var artifact = service.create(getLocalArtifactDesc());

        /* ACT */
        service.getData(artifact.getId(), null);

        /* ASSERT */
        final var localData = dataRepository.findAll().get(0);
        int x = 0;
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private ArtifactDesc getLocalArtifactDesc() {
        final var desc = new ArtifactDesc();
        desc.setTitle("LocalArtifact");
        desc.setValue("Random Value");

        return desc;
    }

    @SneakyThrows
    private ArtifactImpl getLocalArtifact() {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        artifactConstructor.setAccessible(true);

        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, "LocalArtifact");

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private ArtifactImpl getUnknownArtifact() {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        artifactConstructor.setAccessible(true);

        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, "LocalArtifact");

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private LocalData getLocalData() {
        final var dataConstructor = LocalData.class.getConstructor();
        dataConstructor.setAccessible(true);

        final var localData = dataConstructor.newInstance();

        final var idField = localData.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(localData, Long.valueOf(1));

        final var valueField = localData.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(localData, getLocalArtifactDesc().getValue());

        return localData;
    }

    private class UnkownData extends Data{

    }
}
