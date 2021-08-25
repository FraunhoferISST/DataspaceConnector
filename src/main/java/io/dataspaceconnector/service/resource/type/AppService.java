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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppFactory;
import io.dataspaceconnector.model.app.AppImpl;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.repository.AppRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.base.RemoteResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for apps.
 */
@Log4j2
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@Transactional
public class AppService extends BaseEntityService<App, AppDesc> implements RemoteResolver {

    /**
     * The AppStoreService, to get related appstores.
     */
    @Autowired
    private AppStoreService appStoreSvc;

    /**
     * Repository for storing data.
     */
    @Autowired
    private DataRepository dataRepository;

    /**
     * The PortainerRequestService to send request to the local Portainer instance.
     */
    @Autowired
    private PortainerRequestService portainerRequestSvc;


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        return ((AppRepository) getRepository()).identifyByRemoteId(remoteId);
    }

    /**
     * Get AppStores which are offering the given App.
     *
     * @param appId id of the app to find related appstore for.
     * @return Page containing AppStores which are offering an app with AppID.
     */
    public AppStore getAppStoreByAppId(final UUID appId) {
        return appStoreSvc.getAppStoreByAppId(appId);
    }

    /**
     * Update an artifacts underlying data.
     *
     * @param appId The app that should be updated.
     * @param data  The new data.
     * @throws IOException if the data could not be stored.
     */
    @NonNull
    public void setData(final UUID appId, final InputStream data) throws IOException {
        final var app = get(appId);
        final var currentData = ((AppImpl) app).getData();
        if (currentData instanceof LocalData) {
            setAppTemplate(appId, data, (LocalData) currentData);
        } else {
            throw new NotImplemented();
        }
    }

    @NonNull
    private void setAppTemplate(final UUID appArtifactId, final InputStream data,
                                final LocalData localData) throws IOException {
        try {
            // Update the internal database and return the new data.
            final var bytes = data.readAllBytes();
            data.close();
            dataRepository.setLocalData(localData.getId(), bytes);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to store data. [artifactId=({}), exception=({})]",
                        appArtifactId, e.getMessage(), e);
            }

            throw new IOException("Failed to store data.", e);
        }
    }

    /**
     * Update container id of app.
     *
     * @param appId       The id of the app.
     * @param containerID The id of the container.
     */
    public void setContainerIdForApp(final UUID appId, final String containerID) {
        final var app = ((AppImpl) get(appId));
        ((AppFactory) getFactory()).setContainerId(app, containerID);
        getRepository().save(app);
    }

    /**
     * Remove container id from app.
     *
     * @param appId The id of the container.
     */
    public void deleteContainerIdFromApp(final UUID appId) {
        final var app = ((AppImpl) get(appId));
        ((AppFactory) getFactory()).deleteContainerId(app);
        getRepository().save(app);
    }
}
