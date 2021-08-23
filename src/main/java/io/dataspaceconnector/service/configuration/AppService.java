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
package io.dataspaceconnector.service.configuration;

import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppFactory;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.repository.RemoteEntityRepository;
import io.dataspaceconnector.service.resource.BaseEntityService;
import io.dataspaceconnector.util.ErrorMessage;
import io.dataspaceconnector.util.Utils;
import io.dataspaceconnector.util.exception.NotImplemented;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for apps.
 */
@Log4j2
@Service
public class AppService extends BaseEntityService<App, AppDesc> {

    /**
     * The AppStoreService, to get related appstores.
     */
    private final @NonNull AppStoreService appStoreSvc;

    /**
     * Repository for storing data.
     */
    private final @NonNull DataRepository dataRepository;

    /**
     * Constructor for the app service.
     * @param appStoreService The app store service.
     * @param dataRepo        The data repository.
     */
    @Autowired
    public AppService(final @NonNull AppStoreService appStoreService,
                      final @NonNull DataRepository dataRepo) {
        super();
        this.appStoreSvc = appStoreService;
        this.dataRepository = dataRepo;
    }

    /**
     * @return get list of all Apps.
     */
    public List<App> getApps() {
        return getRepository().findAll();
    }

    /**
     * Find app by remoteID.
     *
     * @param remoteID remoteID of the app to find.
     * @return optional of found app.
     */
    public UUID getByRemoteID(final URI remoteID) {
        Utils.requireNonNull(remoteID, ErrorMessage.ENTITYID_NULL);

        final Optional<UUID> entity =
                ((RemoteEntityRepository) getRepository()).identifyByRemoteId(remoteID);

        if (entity.isEmpty()) {
            // Handle with global exception handler
            throw new ResourceNotFoundException(
                    this.getClass().getSimpleName() + ": " + remoteID
            );
        }

        return entity.get();
    }

    /**
     * Get AppStores which are offering the given App.
     * @param appId    id of the app to find related appstore for.
     * @param pageable pageable for response as view.
     * @return Page containing AppStores which are offering an app with AppID.
     */
    public Page<AppStore> getStoresByContainsApp(final UUID appId, final Pageable pageable) {
        return appStoreSvc.getStoresByContainsApp(appId, pageable);
    }

    /**
     * Update an artifacts underlying data.
     * @param appArtifactId The artifact which should be updated.
     * @param data          The new data.
     * @throws IOException if the data could not be stored.
     */
    @NonNull
    public void setData(final UUID appArtifactId, final InputStream data)
            throws IOException {
        final var appArtifact = get(appArtifactId);
        final var currentData = appArtifact.getData();
        if (currentData instanceof LocalData) {
            setAppTemplate(appArtifactId, data, (LocalData) currentData);
        } else {
            throw new NotImplemented();
        }
    }

    @NonNull
    private void setAppTemplate(final UUID appArtifactId, final InputStream data,
                                final LocalData localData)
            throws IOException {
        try {
            // Update the internal database and return the new data.
            final var templateInput = data.readAllBytes();
            data.close();
            dataRepository.setLocalData(localData.getId(), templateInput);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to store data. [artifactId=({}), exception=({})]",
                        appArtifactId, e.getMessage(), e);
            }

            throw new IOException("Failed to store data.", e);
        }
    }

    /**
     * @param appId The id of the app.
     * @param containerID The id of the container.
     */
    public void setContainerIdForApp(final UUID appId, final String containerID) {
        final var app = get(appId);
        ((AppFactory) getFactory()).setContainerId(app, containerID);
        getRepository().save(app);
    }
}
