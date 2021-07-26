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

import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.util.MetadataUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;

/**
 * Creates and updates a data app.
 */
@Component
public class AppFactory extends AbstractNamedFactory<App, AppDesc> {

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
        final var app = new App();
        app.setAppEndpoints(new ArrayList<>());
        app.setKeywords(new ArrayList<>());
        app.setSupportedUsagePolicies(new ArrayList<>());
        return app;
    }

    /**
     * @param app  The entity to be updated.
     * @param desc The description of the new entity.
     * @return True, if app store is updated.
     */
    @Override
    protected boolean updateInternal(final App app, final AppDesc desc) {
        final var hasUpdatedAppDocumentation =
                updateAppDocumentation(app, desc.getAppDocumentation());
        final var hasUpdatedAppEnvironmentVariables =
                updateAppEnvironmentVariables(app, desc.getAppEnvironmentVariables());
        final var hasUpdatedAppStorageConfiguration =
                updateAppStorageConfiguration(app, desc.getAppStorageConfiguration());
        final var hasUpdatedPublisher = updatePublisher(app, desc.getPublisher());
        final var hasUpdatedSovereign = updateSovereign(app, desc.getSovereign());
        final var hasUpdatedLanguage = updateLanguage(app, desc.getLanguage());
        final var hasUpdatedLicense = updateLicense(app, desc.getLicense());
        final var hasUpdatedEndpointDocumentation =
                updateEndpointDocumentation(app, desc.getEndpointDocumentation());
        final var hasUpdatedDataAppDistributionService =
                updateDataAppDistributionService(app, desc.getDataAppDistributionService());
        final var hasUpdatedDataAppRuntimeEnvironment =
                updateDataAppRuntimeEnvironment(app, desc.getDataAppRuntimeEnvironment());
        final var hasUpdatedRemoteId =
                updateRemoteId(app, desc.getRemoteId());
        final var hasUpdatedRemoteAddress =
                updateRemoteAddress(app, desc.getRemoteAddress());

        final var hasUpdated =
                hasUpdatedAppDocumentation || hasUpdatedAppEnvironmentVariables
                        || hasUpdatedAppStorageConfiguration || hasUpdatedPublisher
                        || hasUpdatedLanguage || hasUpdatedSovereign
                        || hasUpdatedLicense || hasUpdatedEndpointDocumentation
                        || hasUpdatedDataAppDistributionService
                        || hasUpdatedDataAppRuntimeEnvironment
                        || hasUpdatedRemoteId || hasUpdatedRemoteAddress;


        if (hasUpdated) {
            app.setVersion(app.getVersion() + 1);
        }
        return hasUpdated;
    }

    private boolean updateRemoteAddress(final App app, final URI remoteAddress) {
        final var newUri = MetadataUtils.updateUri(app.getRemoteAddress(),
                remoteAddress, DEFAULT_URI);
        newUri.ifPresent(app::setRemoteAddress);
        return newUri.isPresent();
    }

    private boolean updateRemoteId(final App app, final URI remoteId) {
        final var newUri = MetadataUtils.updateUri(app.getRemoteId(), remoteId,
                DEFAULT_URI);
        newUri.ifPresent(app::setRemoteId);
        return newUri.isPresent();
    }

    private boolean updateDataAppRuntimeEnvironment(final App app,
                                                    final String dataAppRuntimeEnvironment) {
        final var newValue =
                MetadataUtils.updateString(app.getDataAppRuntimeEnvironment(),
                        dataAppRuntimeEnvironment,
                        DEFAULT_VALUE);
        newValue.ifPresent(app::setDataAppRuntimeEnvironment);

        return newValue.isPresent();

    }

    private boolean updateDataAppDistributionService(final App app,
                                                     final URI dataAppDistributionService) {
        final var newUri =
                MetadataUtils.updateUri(app.getDataAppDistributionService(),
                        dataAppDistributionService,
                DEFAULT_URI);
        newUri.ifPresent(app::setDataAppDistributionService);
        return newUri.isPresent();
    }

    private boolean updateEndpointDocumentation(final App app, final URI endpointDocumentation) {
        final var newUri =
                MetadataUtils.updateUri(app.getEndpointDocumentation(), endpointDocumentation,
                DEFAULT_URI);
        newUri.ifPresent(app::setEndpointDocumentation);
        return newUri.isPresent();
    }

    private boolean updateLicense(final App app, final URI license) {
        final var newUri =
                MetadataUtils.updateUri(app.getLicense(), license,
                DEFAULT_URI);
        newUri.ifPresent(app::setLicense);
        return newUri.isPresent();
    }

    private boolean updateLanguage(final App app, final String language) {
        final var newValue =
                MetadataUtils.updateString(app.getLanguage(), language, DEFAULT_VALUE);
        newValue.ifPresent(app::setLanguage);

        return newValue.isPresent();
    }

    private boolean updateSovereign(final App app, final URI sovereign) {
        final var newUri =
                MetadataUtils.updateUri(app.getSovereign(), sovereign,
                DEFAULT_URI);
        newUri.ifPresent(app::setSovereign);
        return newUri.isPresent();

    }

    private boolean updatePublisher(final App app, final URI publisher) {
        final var newUri = MetadataUtils.updateUri(app.getPublisher(), publisher,
                DEFAULT_URI);
        newUri.ifPresent(app::setPublisher);
        return newUri.isPresent();
    }

    private boolean updateAppStorageConfiguration(final App app,
                                                  final String appStorageConfiguration) {
        final var newValue =
                MetadataUtils.updateString(app.getAppStorageConfiguration(),
                appStorageConfiguration, DEFAULT_VALUE);
        newValue.ifPresent(app::setAppStorageConfiguration);

        return newValue.isPresent();
    }

    private boolean updateAppEnvironmentVariables(final App app,
                                                  final String appEnvironmentVariables) {
        final var newValue =
                MetadataUtils.updateString(app.getAppEnvironmentVariables(),
                appEnvironmentVariables, DEFAULT_VALUE);
        newValue.ifPresent(app::setAppEnvironmentVariables);

        return newValue.isPresent();
    }

    private boolean updateAppDocumentation(final App app, final String appDocumentation) {
        final var newValue = MetadataUtils.updateString(app.getAppDocumentation(),
                appDocumentation, DEFAULT_VALUE);
        newValue.ifPresent(app::setAppDocumentation);

        return newValue.isPresent();
    }
}
