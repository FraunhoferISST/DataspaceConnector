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
import io.dataspaceconnector.model.artifact.Data;
import io.dataspaceconnector.model.artifact.DataType;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.resource.BaseEntityService;
import io.dataspaceconnector.util.exception.NotImplemented;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

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
     **/
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
            final var bytes = data.readAllBytes();
            data.close();
            dataRepository.setAppTemplate(localData.getId(), DataType.APP_TEMPLATE, bytes);

            // Create JSON file for app template.
            final var templates = dataRepository.findAll();
            final var appTemplates = new ArrayList<Data>();
            var jsonTemplate = "";

            for (var dataTmp : templates) {
                if (DataType.APP_TEMPLATE.equals(dataTmp.getType())) {
                    appTemplates.add(dataTmp);
                }
            }

            jsonTemplate = appTemplates.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",", "", ""));

            final var path = Path.of("src/main/resources/Template.json");
            final var  content = Files.readString(path, StandardCharsets.UTF_8);

            final var resultTemplate = String.format(content, jsonTemplate);
            FileUtils.writeStringToFile(new File("src/main/resources/AppTemplate.json"),
                    resultTemplate, StandardCharsets.UTF_8);

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to store data. [artifactId=({}), exception=({})]",
                        appArtifactId, e.getMessage(), e);
            }

            throw new IOException("Failed to store data.", e);
        }
    }
}
