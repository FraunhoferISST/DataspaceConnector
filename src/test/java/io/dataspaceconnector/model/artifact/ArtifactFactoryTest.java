/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.artifact;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32C;

import io.dataspaceconnector.model.auth.ApiKey;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ArtifactFactoryTest {

    private ArtifactFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ArtifactFactory();
    }

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ArtifactFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), ArtifactFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void default_autoDownload_is_false() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertFalse(ArtifactFactory.DEFAULT_AUTO_DOWNLOAD);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_agreementsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getAgreements().size());
    }

    @Test
    public void create_validDesc_representationsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getRepresentations().size());
    }

    @Test
    public void create_validDesc_subscriptionsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getSubscriptions().size());
    }

    /**
     * remoteId.
     */

    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ArtifactFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(URI.create("uri"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), artifact.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setRemoteId(URI.create("uri"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * remoteAddress
     */

    @Test
    public void update_diffrentRemoteAddress_willUpdate() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());
        final var desc = new ArtifactDesc();
        desc.setRemoteAddress(URI.create("https://someWhere"));

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
        Assertions.assertEquals(desc.getRemoteAddress(), artifact.getRemoteAddress());
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ArtifactFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Random Title");

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), artifact.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Random Title");

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * autoDownload.
     */

    @Test
    public void update_differentAutomatedDownload_setAutomatedDownload() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.isAutomatedDownload(), artifact.isAutomatedDownload());
    }

    @Test
    public void update_differentAutomatedDownload_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameAutomatedDownload_returnFalse() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAutomatedDownload(true);

        final var artifact = factory.create(desc);

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), artifact.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullArtifact_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null,
                new ArtifactDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new ArtifactDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }

    /**
     * num accessed
     */

    @Test
    public void create_num_accessed_is_0() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ArtifactDesc());

        /* ASSERT */
        assertEquals(0, result.getNumAccessed());
    }

    /**
     * access url
     */

    @Test
    public void create_nullAccessUrl_isLocalData() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(null);

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(result.getData() instanceof LocalData);
    }

    @Test
    public void create_setEmptyAccessUrl_isLocalData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://"));

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(result.getData() instanceof LocalData);
    }

    @Test
    public void create_setAccessUrl_isRemoteData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertTrue(result.getData() instanceof RemoteData);
    }

    @Test
    public void update_setAccessUrlNull_changeToLocalData() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        final var artifact = factory.create(desc);

        /* ACT */
        factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        assertTrue(((ArtifactImpl) artifact).getData() instanceof LocalData);
    }

    @Test
    public void update_setAccessUrl_changeToRemoteData() throws MalformedURLException {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(((ArtifactImpl) artifact).getData() instanceof RemoteData);
    }

    /**
     * local data
     */

    @Test
    public void create_nullValue_returnEmpty() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setValue(null);

        /* ACT */
        final var result = (ArtifactImpl) factory.create(desc);

        /* ASSERT */
        assertNull(((LocalData) result.getData()).getValue());
    }

    @Test
    public void update_setValue_returnValue() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setValue("Some Value");

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        assertArrayEquals(desc.getValue().getBytes(StandardCharsets.UTF_8), ((LocalData) artifact.getData()).getValue());
    }

    @Test
    public void update_differentValue_returnTrue() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        final var desc = new ArtifactDesc();
        desc.setValue("Random Value");

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameValue_returnFalse() {
        /* ARRANGE */
        final var artifact = (ArtifactImpl) factory.create(new ArtifactDesc());

        /* ACT */
        final var result = factory.update(artifact, new ArtifactDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * access url
     */

    @Test
    public void update_differentAccessUrl_setAccessUrl() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));

        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        factory.update(artifact, desc);

        /* ASSERT */
        final var data = (RemoteData) ((ArtifactImpl) artifact).getData();
        assertEquals(desc.getAccessUrl(), data.getAccessUrl());
    }

    /**
     * basicAuth
     */

    @Test
    public void create_basicAuth_emptyAuthentication() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setBasicAuth(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        final var data = (RemoteData) ((ArtifactImpl) result).getData();
        Assertions.assertTrue(data.getAuthentication().isEmpty());
    }

    @Test
    public void update_differentBasicAuth_setBasicAuth() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setBasicAuth(new AuthenticationDesc("Random Username", "Random Password"));

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setBasicAuth(new AuthenticationDesc("Random Different Username", "Random Password"));

        /* ACT */
        factory.update(artifact, updateDesc);

        /* ASSERT */
        final var data = (RemoteData) ((ArtifactImpl) artifact).getData();
        assertEquals(new BasicAuth(updateDesc.getBasicAuth().getKey(), updateDesc.getBasicAuth().getValue()),
                     data.getAuthentication().get(0));
    }

    /**
     * apiKey
     */

    @Test
    public void create_nullApiKey_nullApiKey() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setApiKey(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        final var data = (RemoteData) ((ArtifactImpl) result).getData();
        Assertions.assertTrue(data.getAuthentication().isEmpty());
    }

    @Test
    public void update_differentApiKey_setApiKey() throws MalformedURLException {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setAccessUrl(new URL("https://localhost:8080/"));
        desc.setApiKey(new AuthenticationDesc("Random Username", "Random Password"));

        final var artifact = factory.create(desc);

        final var updateDesc = new ArtifactDesc();
        updateDesc.setAccessUrl(new URL("https://localhost:8080/"));
        updateDesc.setApiKey(new AuthenticationDesc("Random Username", "Random Different Password"));

        /* ACT */
        factory.update(artifact, updateDesc);

        /* ASSERT */
        final var data = (RemoteData) ((ArtifactImpl) artifact).getData();
        assertEquals(new ApiKey(updateDesc.getApiKey().getKey(), updateDesc.getApiKey().getValue()),
                     data.getAuthentication().get(0));
    }

    @Test
    public void updateByteSize_setByteTooNull_willUpdate() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());
        artifact.setByteSize(20);
        artifact.setCheckSum(3);

        /* ACT */
        final var result = factory.updateByteSize(artifact, null);

        /* ASSERT */
        assertTrue(result);
        assertEquals(0, artifact.getByteSize());
        assertEquals(0, artifact.getCheckSum());
    }

    @Test
    public void updateByteSize_setByteFromNullToEmpty_willNotUpdate() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());

        /* ACT */
        final byte[] data = {};
        final var checksum = new CRC32C();
        checksum.update(data, 0, data.length);
        final var result = factory.updateByteSize(artifact, data);

        /* ASSERT */
        //bytesize and checksum should still be 0
        assertFalse(result);
        assertEquals(0, artifact.getByteSize());
        assertEquals(checksum.getValue(), artifact.getCheckSum());
    }

    @Test
    public void updateByteSize_setByteFromValueToEmptyAndBack_willUpdate() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());
        artifact.setByteSize(20);
        artifact.setCheckSum(3);

        /* ACT */
        final byte[] empty = {};
        final var result1 = factory.updateByteSize(artifact, empty);

        /* ASSERT */
        assertTrue(result1);
        assertEquals(0, artifact.getByteSize());
        assertEquals(0, artifact.getCheckSum());

        /* ACT */
        final byte[] data = {1,2,1,1,23,12,2};
        final var checksum = new CRC32C();
        checksum.update(data, 0, data.length);
        final var result2 = factory.updateByteSize(artifact, data);

        /* ASSERT */
        assertTrue(result2);
        assertEquals(data.length, artifact.getByteSize());
        assertEquals(checksum.getValue(), artifact.getCheckSum());
    }

    @Test
    public void updateByteSize_hasChanged_willUpdateByteSizeAndChecksum() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());
        final byte[] data = {0, 1};
        final var checksum = new CRC32C();
        checksum.update(data, 0, data.length);

        /* ACT */
        final var result = factory.updateByteSize(artifact, data);

        /* ASSERT */
        assertTrue(result);
        assertEquals(2, artifact.getByteSize());
        assertEquals(checksum.getValue(), artifact.getCheckSum());
    }

    @Test
    public void update_allChanged_willUpdate() {
        /* ARRANGE */
        final var artifact = factory.create(new ArtifactDesc());
        final var desc = new ArtifactDesc();
        desc.setValue("someValue");

        /* ACT */
        final var result = factory.update(artifact, desc);

        /* ASSERT */
        assertTrue(result);
    }
}
