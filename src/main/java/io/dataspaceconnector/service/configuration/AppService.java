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

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppImpl;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.BaseEntityService;
import io.dataspaceconnector.util.exception.NotImplemented;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
     * The PortainerRequestService to send request to the local Portainer instance.
     */
    private final @NonNull PortainerRequestService portainerRequestSvc;

    /**
     * Constructor for the app service.
     * @param appStoreService The app store service.
     * @param dataRepo        The data repository.
     */
    @Autowired
    public AppService(final @NonNull AppStoreService appStoreService,
                      final @NonNull DataRepository dataRepo,
                      final @NonNull PortainerRequestService portainerRequestService) {
        super();
        this.appStoreSvc = appStoreService;
        this.dataRepository = dataRepo;
        this.portainerRequestSvc = portainerRequestService;
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
        final var currentData = ((AppImpl) appArtifact).getData();
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

            //TODO: Deploy app via Portainer APIs using infos of template
            //Assuming localhost:9000 is Portainer URL
            final var appStoreTemplate = IOUtils.toString(templateInput, "UTF-8");

            //1. Create registry where APP is hosted in Portainer if not existing:
            // POST http://localhost:9000/api/registries
            //-> Body: Authentication true/false, Name, Password, Type, URL, Username
            //(infos from AppStore Template)
            portainerRequestSvc.createRegistry(appStoreTemplate);

            //2. Pull Image from AppStore registry
            // POST http://localhost:9000/api/endpoints/1/docker/images/create?fromImage=<REGISTRY-URL>%2F<IMAGE>
            // Portainer knows registry URL and credentials if auth required (see step 1)
            // (infos from AppStore Template)
            portainerRequestSvc.pullImage(appStoreTemplate);

            //3. Create volumes if needed (infos from AppStore Template)
            // POST http://localhost:9000/api/endpoints/1/docker/volumes/create
            // return volume ID
            portainerRequestSvc.createVolumes(appStoreTemplate);

            //4.Create Container
            // POST http://localhost:9000/api/endpoints/1/docker/containers/create?name=
            // AppStore-Template as POST request body, volume info in AppStore template
            //       needs to be adjusted to naming/generated volume-id of Step 3
            // returns container ID
            var containerId = portainerRequestSvc.createContainer(appStoreTemplate);

            //5. Start Container
            // POST http://localhost:9000/api/endpoints/1/docker/containers/<CONTAINER-ID>/start
            portainerRequestSvc.startContainer(containerId);

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to store data. [artifactId=({}), exception=({})]",
                        appArtifactId, e.getMessage(), e);
            }

            throw new IOException("Failed to store data.", e);
        }
    }
}
