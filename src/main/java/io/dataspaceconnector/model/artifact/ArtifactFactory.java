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


import io.dataspaceconnector.model.auth.ApiKey;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.CRC32C;

/**
 * Creates and updates an artifact.
 */
public final class ArtifactFactory extends AbstractNamedFactory<Artifact, ArtifactDesc> {

    /**
     * Default remote id assigned to all artifacts.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * Default remote address assigned to all artifacts.
     */
    public static final URI DEFAULT_REMOTE_ADDRESS = URI.create("genesis");

    /**
     * Default download setting assigned to all artifacts.
     */
    public static final boolean DEFAULT_AUTO_DOWNLOAD = false;

    /**
     * Create a new artifact.
     *
     * @param desc The description of the new artifact.
     * @return The new artifact.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    protected Artifact initializeEntity(final ArtifactDesc desc) {
        final var artifact = new ArtifactImpl();
        artifact.setAgreements(new ArrayList<>());
        artifact.setRepresentations(new ArrayList<>());
        artifact.setSubscriptions(new ArrayList<>());

        return artifact;
    }

    @Override
    protected boolean updateInternal(final Artifact artifact, final ArtifactDesc desc) {
        final var hasUpdatedRemoteId = updateRemoteId(artifact, desc.getRemoteId());
        final var hasUpdatedRemoteAddress = updateRemoteAddress(artifact, desc.getRemoteAddress());
        final var hasUpdatedAutoDownload = updateAutoDownload(artifact, desc.isAutomatedDownload());
        final var hasUpdatedData = updateData(artifact, desc);

        return hasUpdatedRemoteId || hasUpdatedRemoteAddress || hasUpdatedAutoDownload
                || hasUpdatedData;
    }

    private boolean updateRemoteId(final Artifact artifact, final URI remoteId) {
        final var newUri =
                FactoryUtils.updateUri(artifact.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(artifact::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateRemoteAddress(final Artifact artifact, final URI remoteAddress) {
        final var newUri = FactoryUtils
                .updateUri(artifact.getRemoteAddress(), remoteAddress, DEFAULT_REMOTE_ADDRESS);
        newUri.ifPresent(artifact::setRemoteAddress);

        return newUri.isPresent();
    }

    private boolean updateAutoDownload(final Artifact artifact, final boolean autoDownload) {
        if (artifact.isAutomatedDownload() != autoDownload) {
            artifact.setAutomatedDownload(autoDownload);
            return true;
        }

        return false;
    }

    private boolean updateData(final Artifact artifact, final ArtifactDesc desc) {
        return isRemoteData(desc)
            ? updateRemoteData((ArtifactImpl) artifact, desc.getAccessUrl(),
                                          desc.getBasicAuth(), desc.getApiKey())
            : updateLocalData((ArtifactImpl) artifact, desc.getValue());
    }

    private static boolean isRemoteData(final ArtifactDesc desc) {
        return desc.getAccessUrl() != null && !desc.getAccessUrl().getAuthority().isBlank();
    }

    private boolean updateLocalData(final ArtifactImpl artifact, final String value) {
        final var data = new LocalData();
        data.setValue(value == null ? null : value.getBytes(StandardCharsets.UTF_8));

        final var currentData = artifact.getData();
        if (currentData instanceof LocalData) {
            if (!currentData.equals(data)) {
                setLocalArtifactData(artifact, data);
                return true;
            }
        } else {
            setLocalArtifactData(artifact, data);
            return true;
        }

        return false;
    }

    private void setLocalArtifactData(final ArtifactImpl artifact, final LocalData data) {
        artifact.setData(data);
        updateByteSize(artifact, data.getValue());
    }

    private boolean updateRemoteData(final ArtifactImpl artifact, final URL accessUrl,
                                     final AuthenticationDesc basicAuth,
                                     final AuthenticationDesc apiKey) {
        final var newData = new RemoteData();
        newData.setAuthentication(new ArrayList<>());
        newData.setAccessUrl(accessUrl);
        if (basicAuth != null) {
            newData.addAuthentication(new BasicAuth(basicAuth.getKey(), basicAuth.getValue()));
        }
        if (apiKey != null) {
            newData.addAuthentication(new ApiKey(apiKey.getKey(), apiKey.getValue()));
        }

        final var oldData = artifact.getData();
        if (oldData instanceof RemoteData) {
            if (!oldData.equals(newData)) {
                artifact.setData(newData);
                return true;
            }
        } else {
            artifact.setData(newData);
            return true;
        }

        return false;
    }

    /**
     * Update the byte and checksum of an artifact. This will not update the actual data.
     *
     * @param artifact The artifact which byte and checksum needs to be recalculated.
     * @param bytes    The data.
     * @return true if the artifact has been modified.
     */
    public boolean updateByteSize(final Artifact artifact, final byte[] bytes) {
        if (bytes != null) {
            final var byteSize = bytes.length;
            final var checkSum = calculateChecksum(bytes);
            if (artifact.getCheckSum() != checkSum || artifact.getByteSize() != byteSize) {
                setByteSizeAndCheckSum(artifact, byteSize, checkSum);
                return true;
            }
        } else {
            if (artifact.getByteSize() != 0 || artifact.getCheckSum() != 0) {
                setByteSizeAndCheckSum(artifact, 0, 0);
                return true;
            }
        }
        return false;
    }

    private void setByteSizeAndCheckSum(
            final Artifact artifact,
            final long byteSize,
            final long checkSum
    ) {
        //Update fields of artifact
        artifact.setByteSize(byteSize);
        artifact.setCheckSum(checkSum);
    }


    private long calculateChecksum(final byte[] bytes) {
        if (bytes == null) {
            return 0;
        }

        final var checksum = new CRC32C();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
