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

import io.dataspaceconnector.model.AbstractNamedFactory;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import io.dataspaceconnector.util.MetadataUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Creates and updates a configuration.
 */
@Component
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
     * Default log level.
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;

    /**
     * Default deploy mode.
     */
    public static final DeployMode DEFAULT_DEPLOY_MODE = DeployMode.TEST;

    /**
     * Default connector endpoint.
     */
    private static final URI DEFAULT_CONNECTOR_ENDPOINT =
            URI.create("https://localhost:8080/api/ids/data");

    /**
     * The default version.
     */
    private static final String DEFAULT_VERSION = "6.0.0";

    /**
     * The default outbound model version.
     */
    private static final String DEFAULT_OUTBOUND_VERSION = "4.1.0";

    /**
     * The default maintainer.
     */
    private static final URI DEFAULT_MAINTAINER = URI.create("https://www.isst.fraunhofer.de/");

    /**
     * The default curator.
     */
    private static final URI DEFAULT_CURATOR = URI.create("https://www.isst.fraunhofer.de/");

    /**
     * @param desc The description of the entity.
     * @return The new configuration entity.
     */
    @Override
    protected Configuration initializeEntity(final ConfigurationDesc desc) {
        final var config = new Configuration();
        config.setLogLevel(DEFAULT_LOG_LEVEL);
        config.setDeployMode(DEFAULT_DEPLOY_MODE);
        config.setInboundModelVersion(new ArrayList<>());

        return config;
    }

    /**
     * @param config The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if configuration is updated.
     */
    @Override
    protected boolean updateInternal(final Configuration config, final ConfigurationDesc desc) {
        final var hasUpdatedConnectorEndpoint = updateConnectorEndpoint(config,
                desc.getConnectorEndpoint());
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
        final var hasUpdatedTrustStore = updateTrustStore(config, desc.getTruststoreSettings());
        final var hasUpdatedKeyStore = updateKeyStore(config, desc.getKeystoreSettings());
        final var hasUpdatedProxy = updateProxy(config, desc.getProxySettings());

        return hasUpdatedConnectorEndpoint
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
                || hasUpdatedProxy;
    }

    /**
     * @param config The configuration.
     * @param securityProfile The new security profile.
     * @return True, if security profile is updated.
     */
    private boolean updateSecurityProfile(final Configuration config,
                                          final SecurityProfile securityProfile) {
        config.setSecurityProfile(Objects.requireNonNullElse(securityProfile,
                SecurityProfile.BASE_SECURITY));
        return true;
    }

    /**
     * @param config The configuration.
     * @param outboundModelVersion The outbound model version.
     * @return True, if outbound model version is updated.
     */
    private boolean updateOutboundModelVersion(final Configuration config,
                                               final String outboundModelVersion) {
        final var newOutboundVersion =
                MetadataUtils.updateString(config.getOutboundModelVersion(), outboundModelVersion,
                        DEFAULT_OUTBOUND_VERSION);
        newOutboundVersion.ifPresent(config::setOutboundModelVersion);

        return newOutboundVersion.isPresent();
    }

    /**
     * @param config The configuration.
     * @param inboundModelVersion The new inbound model version list.
     * @return True, if list is updated.
     */
    private boolean updateInboundModelVersion(final Configuration config,
                                              final List<String> inboundModelVersion) {
        final var newInboundModelVersionList =
                MetadataUtils.updateStringList(config.getInboundModelVersion(), inboundModelVersion,
                        new ArrayList<>());
        newInboundModelVersionList.ifPresent(config::setInboundModelVersion);

        return newInboundModelVersionList.isPresent();
    }

    /**
     * @param config The configuration.
     * @param maintainer The new maintainer.
     * @return True, if maintainer is updated.
     */
    private boolean updateMaintainer(final Configuration config, final URI maintainer) {
        final var newUri =
                MetadataUtils.updateUri(config.getMaintainer(), maintainer,
                        DEFAULT_MAINTAINER);
        newUri.ifPresent(config::setMaintainer);

        return newUri.isPresent();
    }

    /**
     * @param config The configuration.
     * @param curator The new curator.
     * @return True, if curator is updated.
     */
    private boolean updateCurator(final Configuration config, final URI curator) {
        final var newUri =
                MetadataUtils.updateUri(config.getMaintainer(), curator,
                        DEFAULT_CURATOR);
        newUri.ifPresent(config::setCurator);

        return newUri.isPresent();
    }

    /**
     * @param config The configuration.
     * @param version The updated project version.
     * @return True, if version is updated.
     */
    private boolean updateVersion(final Configuration config,
                                  final String version) {
        final var newVersion = MetadataUtils.updateString(config.getVersion(),
                version, DEFAULT_VERSION);
        newVersion.ifPresent(config::setVersion);

        return newVersion.isPresent();
    }

    /**
     * @param config The configuration
     * @param connectorEndpoint The new connector endpoint.
     * @return True, if connector endpoint is updated.
     */
    private boolean updateConnectorEndpoint(final Configuration config,
                                            final URI connectorEndpoint) {
        final var newUri =
                MetadataUtils.updateUri(config.getConnectorEndpoint(), connectorEndpoint,
                        DEFAULT_CONNECTOR_ENDPOINT);
        newUri.ifPresent(config::setConnectorEndpoint);

        return newUri.isPresent();
    }

    private boolean updateKeyStore(final Configuration config, final KeystoreDesc desc) {
        if (desc != null
                && config.getKeystore() != null
                && config.getKeystore().getLocation() != null
                && config.getKeystore().getLocation().equals(desc.getLocation())
                && config.getKeystore().getPassword() != null
                && config.getKeystore().getPassword().equals(desc.getPassword())) {
            return false;
        }

        config.setKeystore(keystoreFactory.create(desc == null ? new KeystoreDesc() : desc));
        return true;
    }

    private boolean updateTrustStore(final Configuration config, final TruststoreDesc desc) {
        if (desc != null
                && config.getTruststore() != null
                && config.getTruststore().getLocation() != null
                && config.getTruststore().getLocation().equals(desc.getLocation())
                && config.getTruststore().getPassword() != null
                && config.getTruststore().getPassword().equals(desc.getPassword())) {
            return false;
        }

        config.setTruststore(truststoreFactory.create(desc == null ? new TruststoreDesc() : desc));
        return true;
    }

    private boolean updateDeployMode(final Configuration config,
                                     final DeployMode deployMode) {
        final var tmp = deployMode == null ? DEFAULT_DEPLOY_MODE : deployMode;
        if (config.getDeployMode().equals(tmp)) {
            return false;
        }

        config.setDeployMode(tmp);
        return true;
    }

    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        final var tmp = logLevel == null ? DEFAULT_LOG_LEVEL : logLevel;
        if (config.getLogLevel().equals(tmp)) {
            return false;
        }

        config.setLogLevel(tmp);
        return true;
    }

    private boolean updateProxy(final Configuration configuration, final ProxyDesc desc) {
        if (configuration.getProxy() == null && desc == null) {
            return false;
        }

        if (configuration.getProxy() != null && desc == null) {
            configuration.setProxy(null);
            return true;
        }

        configuration.setProxy(proxyFactory.create(desc));
        return true;
    }
}
