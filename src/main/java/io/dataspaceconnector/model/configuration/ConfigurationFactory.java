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
package io.dataspaceconnector.model.configuration;

import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.net.URI;
import java.util.List;

/**
 * Creates and updates a configuration.
 */
@RequiredArgsConstructor
public class ConfigurationFactory extends AbstractNamedFactory<Configuration, ConfigurationDesc> {

    /**
     * Contains creation and update logic for proxy objects.
     **/
    private final @NonNull ProxyFactory proxyFactory;

    /**
     * Contains creation and update logic for truststore objects.
     **/
    private final @NonNull TruststoreFactory truststoreFactory;

    /**
     * Contains creation and update logic for keystore objects.
     **/
    private final @NonNull KeystoreFactory keystoreFactory;

    /**
     * Service for connector configurations.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Default log level.
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;

    /**
     * Default deploy mode.
     */
    public static final DeployMode DEFAULT_DEPLOY_MODE = DeployMode.TEST;

    /**
     * Default connector id.
     */
    public static final URI DEFAULT_CONNECTOR_ID = URI.create("https://localhost:8080");

    /**
     * Default connector endpoint.
     */
    public static final String DEFAULT_ENDPOINT = "/api/ids/data";

    /**
     * The default maintainer.
     */
    public static final URI DEFAULT_MAINTAINER = URI.create("https://www.isst.fraunhofer.de/");

    /**
     * The default curator.
     */
    public static final URI DEFAULT_CURATOR = URI.create("https://www.isst.fraunhofer.de/");

    /**
     * The default security profile.
     */
    public static final SecurityProfile DEFAULT_SECURITY_PROFILE = SecurityProfile.BASE_SECURITY;

    /**
     * The default connector status.
     */
    public static final ConnectorStatus DEFAULT_STATUS = ConnectorStatus.ONLINE;

    /**
     * @param desc The description of the entity.
     * @return The new configuration entity.
     */
    @Override
    protected Configuration initializeEntity(final ConfigurationDesc desc) {
        return new Configuration();
    }

    /**
     * @param config The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if configuration is updated.
     */
    @Override
    protected boolean updateInternal(final Configuration config, final ConfigurationDesc desc) {
        final var hasUpdatedConnectorId = updateConnectorId(config, desc.getConnectorId());
        final var hasUpdatedDefaultEndpoint = updateDefaultEndpoint(config,
                desc.getDefaultEndpoint());
        final var hasUpdatedVersion = updateVersion(config, desc.getVersion());
        final var hasUpdatedCurator = updateCurator(config, desc.getCurator());
        final var hasUpdatedMaintainer = updateMaintainer(config, desc.getMaintainer());
        final var hasUpdatedInboundModelVersions = updateInboundModelVersion(config,
                desc.getInboundModelVersion());
        final var hasUpdatedOutboundModelVersion = updateOutboundModelVersion(config,
                desc.getOutboundModelVersion());
        final var hasUpdatedSecurityProfile = updateSecurityProfile(config,
                desc.getSecurityProfile());
        final var hasUpdatedLogLevel = updateLogLevel(config, desc.getLogLevel());
        final var hasUpdatedDeployMode = updateDeployMode(config, desc.getDeployMode());
        final var hasUpdatedTrustStore = updateTrustStore(config, desc.getTruststore());
        final var hasUpdatedKeyStore = updateKeyStore(config, desc.getKeystore());
        final var hasUpdatedProxy = updateProxy(config, desc.getProxy());
        final var hasUpdatedStatus = updateStatus(config, desc.getStatus());

        return hasUpdatedDefaultEndpoint
                || hasUpdatedConnectorId
                || hasUpdatedVersion
                || hasUpdatedCurator
                || hasUpdatedMaintainer
                || hasUpdatedInboundModelVersions
                || hasUpdatedOutboundModelVersion
                || hasUpdatedSecurityProfile
                || hasUpdatedLogLevel
                || hasUpdatedDeployMode
                || hasUpdatedTrustStore
                || hasUpdatedKeyStore
                || hasUpdatedProxy
                || hasUpdatedStatus;
    }

    /**
     * @param config          The configuration.
     * @param securityProfile The new security profile.
     * @return True, if security profile is updated.
     */
    private boolean updateSecurityProfile(final Configuration config,
                                          final SecurityProfile securityProfile) {
        final var tmp = securityProfile == null ? DEFAULT_SECURITY_PROFILE : securityProfile;
        if (tmp.equals(config.getSecurityProfile())) {
            return false;
        }

        config.setSecurityProfile(tmp);
        return true;
    }

    /**
     * @param config               The configuration.
     * @param outboundModelVersion The outbound model version.
     * @return True, if outbound model version is updated.
     */
    private boolean updateOutboundModelVersion(final Configuration config,
                                               final String outboundModelVersion) {
        final var newOutboundVersion =
                FactoryUtils.updateString(config.getOutboundModelVersion(), outboundModelVersion,
                        connectorConfig.getOutboundVersion());
        newOutboundVersion.ifPresent(config::setOutboundModelVersion);

        return newOutboundVersion.isPresent();
    }

    /**
     * @param config              The configuration.
     * @param inboundModelVersion The new inbound model version list.
     * @return True, if list is updated.
     */
    private boolean updateInboundModelVersion(final Configuration config,
                                              final List<String> inboundModelVersion) {
        final var newInboundModelVersionList =
                FactoryUtils.updateStringList(config.getInboundModelVersion(), inboundModelVersion,
                        connectorConfig.getInboundVersions());
        newInboundModelVersionList.ifPresent(config::setInboundModelVersion);

        return newInboundModelVersionList.isPresent();
    }

    /**
     * @param config      The configuration.
     * @param connectorId The new connector id.
     * @return True, if connector id is updated.
     */
    private boolean updateConnectorId(final Configuration config, final URI connectorId) {
        final var newUri = FactoryUtils.updateUri(config.getConnectorId(),
                connectorId, DEFAULT_CONNECTOR_ID);
        newUri.ifPresent(config::setConnectorId);

        return newUri.isPresent();
    }

    /**
     * @param config     The configuration.
     * @param maintainer The new maintainer.
     * @return True, if maintainer is updated.
     */
    private boolean updateMaintainer(final Configuration config, final URI maintainer) {
        final var newUri =
                FactoryUtils.updateUri(config.getMaintainer(), maintainer,
                        DEFAULT_MAINTAINER);
        newUri.ifPresent(config::setMaintainer);

        return newUri.isPresent();
    }

    /**
     * @param config  The configuration.
     * @param curator The new curator.
     * @return True, if curator is updated.
     */
    private boolean updateCurator(final Configuration config, final URI curator) {
        final var newUri =
                FactoryUtils.updateUri(config.getCurator(), curator,
                        DEFAULT_CURATOR);
        newUri.ifPresent(config::setCurator);

        return newUri.isPresent();
    }

    /**
     * @param config  The configuration.
     * @param version The updated project version.
     * @return True, if version is updated.
     */
    private boolean updateVersion(final Configuration config,
                                  final String version) {
        final var newVersion = FactoryUtils.updateString(config.getVersion(),
                version, connectorConfig.getDefaultVersion());
        newVersion.ifPresent(config::setVersion);

        return newVersion.isPresent();
    }

    /**
     * @param config          The configuration
     * @param defaultEndpoint The new connector endpoint.
     * @return True, if connector endpoint is updated.
     */
    private boolean updateDefaultEndpoint(final Configuration config, final URI defaultEndpoint) {
        final var newUri = FactoryUtils.updateUri(config.getDefaultEndpoint(),
                defaultEndpoint, URI.create(config.getConnectorId() + DEFAULT_ENDPOINT));
        newUri.ifPresent(config::setDefaultEndpoint);

        return newUri.isPresent();
    }

    private boolean updateKeyStore(final Configuration config, final KeystoreDesc desc) {
        final var tmp = keystoreFactory.create(desc == null ? new KeystoreDesc() : desc);
        if (tmp.equals(config.getKeystore())) {
            return false;
        }

        config.setKeystore(tmp);
        return true;
    }

    private boolean updateTrustStore(final Configuration config, final TruststoreDesc desc) {
        final var tmp = truststoreFactory.create(desc == null ? new TruststoreDesc() : desc);
        if (tmp.equals(config.getTruststore())) {
            return false;
        }

        config.setTruststore(tmp);
        return true;
    }

    private boolean updateDeployMode(final Configuration config,
                                     final DeployMode deployMode) {
        final var tmp = deployMode == null ? DEFAULT_DEPLOY_MODE : deployMode;
        if (tmp.equals(config.getDeployMode())) {
            return false;
        }

        config.setDeployMode(tmp);
        return true;
    }

    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        final var tmp = logLevel == null ? DEFAULT_LOG_LEVEL : logLevel;
        if (tmp.equals(config.getLogLevel())) {
            return false;
        }

        config.setLogLevel(tmp);
        return true;
    }

    private boolean updateProxy(final Configuration configuration, final ProxyDesc desc) {
        if (configuration.getProxy() != null && desc == null) {
            configuration.setProxy(null);
            return true;
        }

        if (configuration.getProxy() == null && desc == null) {
            return false;
        }

        if (configuration.getProxy() != null) {
            return proxyFactory.update(configuration.getProxy(), desc);
        } else {
            configuration.setProxy(proxyFactory.create(desc));
            return true;
        }
    }

    private boolean updateStatus(final Configuration configuration, final ConnectorStatus status) {
        final var tmp = status == null ? DEFAULT_STATUS : status;

        if (tmp.equals(configuration.getStatus())) {
            return false;
        }

        configuration.setStatus(tmp);
        return true;
    }
}
