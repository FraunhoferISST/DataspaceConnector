/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

/**
 * Creates and updates a data app.
 */
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
     * The default keywords assigned to all apps.
     */
    public static final List<String> DEFAULT_KEYWORDS = List.of("DSC");

    /**
     * {@inheritDoc}
     */
    @Override
    protected App initializeEntity(final AppDesc desc) {
        final var app = new AppImpl();

        app.setEndpoints(new ArrayList<>());

        return app;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean updateInternal(final App app, final AppDesc desc) {
        final var hasUpdatedDocumentation = updateDocumentation(app, desc.getDocs());
        final var hasUpdatedKeywords = updateKeywords(app, desc.getKeywords());
        final var hadUpdatedPolicies = updatePolicies(app, desc.getSupportedPolicies());
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
                || hasUpdatedRemoteId || hasUpdatedRemoteAddress || hasUpdatedData
                || hasUpdatedKeywords || hadUpdatedPolicies;

        if (hasUpdated) {
            app.setVersion(app.getVersion() + 1);
        }
        return hasUpdated;
    }

    /**
     * @param app the app to update.
     * @param value updated data field.
     * @return true, if update was successful.
     */
    private boolean updateData(final AppImpl app, final String value) {
        final var newData = new LocalData();
        final var data = value == null ? null : value.getBytes(StandardCharsets.UTF_8);
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

    /**
     * @param app the app to update.
     * @param remoteAddress new remoteAddress.
     * @return true, if update was successful.
     */
    private boolean updateRemoteAddress(final App app, final URI remoteAddress) {
        final var newUri = FactoryUtils.updateUri(app.getRemoteAddress(), remoteAddress,
                DEFAULT_REMOTE_ADDRESS);
        newUri.ifPresent(app::setRemoteAddress);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param keywords new keywords.
     * @return true, if update was successful.
     */
    private boolean updateKeywords(final App app, final List<String> keywords) {
        final var newKeys =
                FactoryUtils.updateStringList(app.getKeywords(), keywords, DEFAULT_KEYWORDS);
        newKeys.ifPresent(app::setKeywords);

        return newKeys.isPresent();
    }

    /**
     * @param app the app to update.
     * @param policies new policies.
     * @return true, if update was successful.
     */
    private boolean updatePolicies(final App app, final List<PolicyPattern> policies) {
        final var newList = FactoryUtils.updatePolicyList(
                app.getSupportedPolicies(), policies, new ArrayList<>());
        newList.ifPresent(app::setSupportedPolicies);

        return newList.isPresent();
    }

    /**
     * @param app the app to update.
     * @param remoteId new remoteId.
     * @return true, if update was successful.
     */
    private boolean updateRemoteId(final App app, final URI remoteId) {
        final var newUri = FactoryUtils.updateUri(app.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(app::setRemoteId);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param runtimeEnvironment new runtimeEnvironment.
     * @return true, if update was successful.
     */
    private boolean updateRuntimeEnvironment(final App app, final String runtimeEnvironment) {
        final var newValue = FactoryUtils.updateString(app.getRuntimeEnvironment(),
                runtimeEnvironment, DEFAULT_VALUE);
        newValue.ifPresent(app::setRuntimeEnvironment);

        return newValue.isPresent();

    }

    /**
     * @param app the app to update.
     * @param distributionService new distributionService.
     * @return true, if update was successful.
     */
    private boolean updateDistributionService(final App app, final URI distributionService) {
        final var newUri = FactoryUtils.updateUri(app.getDistributionService(),
                distributionService, DEFAULT_URI);
        newUri.ifPresent(app::setDistributionService);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param endpointDocumentation new endpointDocumentation.
     * @return true, if update was successful.
     */
    private boolean updateEndpointDocs(final App app, final URI endpointDocumentation) {
        final var newUri = FactoryUtils.updateUri(app.getEndpointDocumentation(),
                endpointDocumentation, DEFAULT_URI);
        newUri.ifPresent(app::setEndpointDocumentation);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param license new license.
     * @return true, if update was successful.
     */
    private boolean updateLicense(final App app, final URI license) {
        final var newUri = FactoryUtils.updateUri(app.getLicense(), license, DEFAULT_URI);
        newUri.ifPresent(app::setLicense);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param language new language.
     * @return true, if update was successful.
     */
    private boolean updateLanguage(final App app, final String language) {
        final var newValue = FactoryUtils.updateString(app.getLanguage(), language, DEFAULT_VALUE);
        newValue.ifPresent(app::setLanguage);

        return newValue.isPresent();
    }

    /**
     * @param app the app to update.
     * @param sovereign new sovereign.
     * @return true, if update was successful.
     */
    private boolean updateSovereign(final App app, final URI sovereign) {
        final var newUri = FactoryUtils.updateUri(app.getSovereign(), sovereign, DEFAULT_URI);
        newUri.ifPresent(app::setSovereign);

        return newUri.isPresent();

    }

    /**
     * @param app the app to update.
     * @param publisher new publisher.
     * @return true, if update was successful.
     */
    private boolean updatePublisher(final App app, final URI publisher) {
        final var newUri = FactoryUtils.updateUri(app.getPublisher(), publisher, DEFAULT_URI);
        newUri.ifPresent(app::setPublisher);

        return newUri.isPresent();
    }

    /**
     * @param app the app to update.
     * @param storageConfig new storage configuration.
     * @return true, if update was successful.
     */
    private boolean updateStorageConfig(final App app, final String storageConfig) {
        final var newValue = FactoryUtils.updateString(app.getStorageConfig(),
                storageConfig, DEFAULT_VALUE);
        newValue.ifPresent(app::setStorageConfig);

        return newValue.isPresent();
    }

    /**
     * @param app the app to update.
     * @param envVariables new environment variables.
     * @return true, if update was successful.
     */
    private boolean updateEnvVariables(final App app, final String envVariables) {
        final var newValue = FactoryUtils.updateString(app.getEnvVariables(), envVariables,
                DEFAULT_VALUE);
        newValue.ifPresent(app::setEnvVariables);

        return newValue.isPresent();
    }

    /**
     * @param app the app to update.
     * @param docs new docs.
     * @return true, if update was successful.
     */
    private boolean updateDocumentation(final App app, final String docs) {
        final var newValue = FactoryUtils.updateString(app.getDocs(), docs, DEFAULT_VALUE);
        newValue.ifPresent(app::setDocs);

        return newValue.isPresent();
    }

    /**
     * Set containerID to AppImpl.
     *
     * @param app         The app entity.
     * @param containerId The id of the container which is set.
     */
    public void setContainerId(final AppImpl app, final String containerId) {
        app.setContainerId(containerId);
    }

    /**
     * Delete containerId from AppImpl.
     *
     * @param app The app entity.
     */
    public void deleteContainerId(final AppImpl app) {
        app.setContainerId(null);
    }

    /**
     * @param app The app entity.
     * @param name The name of the container.
     */
    public void setContainerName(final AppImpl app, final String name) {
        app.setContainerName(name);
    }
}
