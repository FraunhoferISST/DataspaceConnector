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
package io.dataspaceconnector.model.app;

import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Creates and updates a data app.
 */
@Component
public class AppFactory extends AbstractNamedFactory<App, AppDesc> {

    /**
     * The default remote id assigned to all apps.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * Default remote address assigned to all apps.
     */
    public static final URI DEFAULT_REMOTE_ADDRESS = URI.create("genesis");

    /**
     * The default value.
     */
    public static final String DEFAULT_VALUE = "";

    /**
     * Default access url.
     */
    public static final URI DEFAULT_URI = URI.create("https://app.com");

    /**
     * {@inheritDoc}
     */
    @Override
    protected App initializeEntity(final AppDesc desc) {
        final var app = new AppImpl();

        app.setEndpoints(new ArrayList<>());
        app.setKeywords(new ArrayList<>());
        app.setSupportedPolicies(new ArrayList<>());

        return app;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean updateInternal(final App app, final AppDesc desc) {
        final var hasUpdatedDocumentation = updateDocumentation(app, desc.getDocs());
        final var hasUpdatedEnvVariables = updateEnvVariables(app, desc.getEnvVariables());
        final var hasUpdatedStorageConfig = updateStorageConfig(app, desc.getStorageConfig());
        final var hasUpdatedPublisher = updatePublisher(app, desc.getPublisher());
        final var hasUpdatedSovereign = updateSovereign(app, desc.getSovereign());
        final var hasUpdatedLanguage = updateLanguage(app, desc.getLanguage());
        final var hasUpdatedLicense = updateLicense(app, desc.getLicense());
        final var hasUpdatedEndpointDocs = updateEndpointDocs(app, desc.getEndpointDocumentation());
        final var hasUpdatedDistributionService
                = updateDistributionService(app, desc.getDistributionService());
        final var hasUpdatedRuntimeEnvironment
                = updateRuntimeEnvironment(app, desc.getRuntimeEnvironment());
        final var hasUpdatedRemoteId = updateRemoteId(app, desc.getRemoteId());
        final var hasUpdatedRemoteAddress = updateRemoteAddress(app, desc.getRemoteAddress());

        final var hasUpdatedData = updateData((AppImpl) app, desc.getValue());

        final var hasUpdated = hasUpdatedDocumentation || hasUpdatedEnvVariables
                || hasUpdatedStorageConfig || hasUpdatedPublisher || hasUpdatedLanguage
                || hasUpdatedSovereign || hasUpdatedLicense || hasUpdatedEndpointDocs
                || hasUpdatedDistributionService || hasUpdatedRuntimeEnvironment
                || hasUpdatedRemoteId || hasUpdatedRemoteAddress || hasUpdatedData;

        if (hasUpdated) {
            app.setVersion(app.getVersion() + 1);
        }
        return hasUpdated;
    }

    private boolean updateData(final AppImpl app, final String value) {
        final var newData = new LocalData();
        final var data = value == null ? null : value.getBytes(StandardCharsets.UTF_16);
        newData.setValue(data);

        final var oldData = app.getData();
        if (oldData instanceof LocalData) {
            if (!oldData.equals(newData)) {
                app.setData(newData);
                return true;
            }
        } else {
            app.setData(newData);
            return true;
        }

        return false;
    }

    private boolean updateRemoteAddress(final App app, final URI remoteAddress) {
        final var newUri = FactoryUtils.updateUri(app.getRemoteAddress(), remoteAddress,
                DEFAULT_REMOTE_ADDRESS);
        newUri.ifPresent(app::setRemoteAddress);

        return newUri.isPresent();
    }

    private boolean updateRemoteId(final App app, final URI remoteId) {
        final var newUri = FactoryUtils.updateUri(app.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(app::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateRuntimeEnvironment(final App app, final String runtimeEnvironment) {
        final var newValue = FactoryUtils.updateString(app.getRuntimeEnvironment(),
                runtimeEnvironment, DEFAULT_VALUE);
        newValue.ifPresent(app::setRuntimeEnvironment);

        return newValue.isPresent();

    }

    private boolean updateDistributionService(final App app, final URI distributionService) {
        final var newUri = FactoryUtils.updateUri(app.getDistributionService(),
                distributionService, DEFAULT_URI);
        newUri.ifPresent(app::setDistributionService);

        return newUri.isPresent();
    }

    private boolean updateEndpointDocs(final App app, final URI endpointDocumentation) {
        final var newUri = FactoryUtils.updateUri(app.getEndpointDocumentation(),
                endpointDocumentation, DEFAULT_URI);
        newUri.ifPresent(app::setEndpointDocumentation);

        return newUri.isPresent();
    }

    private boolean updateLicense(final App app, final URI license) {
        final var newUri = FactoryUtils.updateUri(app.getLicense(), license, DEFAULT_URI);
        newUri.ifPresent(app::setLicense);

        return newUri.isPresent();
    }

    private boolean updateLanguage(final App app, final String language) {
        final var newValue = FactoryUtils.updateString(app.getLanguage(), language, DEFAULT_VALUE);
        newValue.ifPresent(app::setLanguage);

        return newValue.isPresent();
    }

    private boolean updateSovereign(final App app, final URI sovereign) {
        final var newUri = FactoryUtils.updateUri(app.getSovereign(), sovereign, DEFAULT_URI);
        newUri.ifPresent(app::setSovereign);

        return newUri.isPresent();

    }

    private boolean updatePublisher(final App app, final URI publisher) {
        final var newUri = FactoryUtils.updateUri(app.getPublisher(), publisher, DEFAULT_URI);
        newUri.ifPresent(app::setPublisher);

        return newUri.isPresent();
    }

    private boolean updateStorageConfig(final App app, final String storageConfig) {
        final var newValue = FactoryUtils.updateString(app.getStorageConfig(),
                storageConfig, DEFAULT_VALUE);
        newValue.ifPresent(app::setStorageConfig);

        return newValue.isPresent();
    }

    private boolean updateEnvVariables(final App app, final String envVariables) {
        final var newValue = FactoryUtils.updateString(app.getEnvVariables(), envVariables,
                DEFAULT_VALUE);
        newValue.ifPresent(app::setEnvVariables);

        return newValue.isPresent();
    }

    private boolean updateDocumentation(final App app, final String docs) {
        final var newValue = FactoryUtils.updateString(app.getDocs(), docs, DEFAULT_VALUE);
        newValue.ifPresent(app::setDocs);

        return newValue.isPresent();
    }

    /**
     * @param app         The app entity.
     * @param containerId The id of the container which is set.
     */
    public void setContainerId(final AppImpl app, final String containerId) {
        // TODO Keep in AppImpl?
        app.setContainerId(containerId);
    }

    /**
     * @param app The app entity.
     */
    public void deleteContainerId(final App app) {
        app.setContainerID(null);
    }
}
