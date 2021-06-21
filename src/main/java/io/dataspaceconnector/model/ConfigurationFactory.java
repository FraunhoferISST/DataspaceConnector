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
package io.dataspaceconnector.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a configuration.
 */
@Component
@RequiredArgsConstructor
public class ConfigurationFactory implements AbstractFactory<Configuration, ConfigurationDesc> {

    private final @NonNull ProxyFactory proxyFactory;

    private final @NonNull TruststoreFactory truststoreFactory;

    private final @NonNull KeystoreFactory keystoreFactory;

    /**
     * @param desc The description of the entity.
     * @return The new configuration entity.
     */
    @Override
    public Configuration create(final ConfigurationDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.MESSAGE_NULL);

        final var config = new Configuration();

        update(config, desc);
        return config;
    }

    /**
     * @param config The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if configuration is updated.
     */
    @Override
    public boolean update(final Configuration config, final ConfigurationDesc desc) {
        Utils.requireNonNull(config, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedLogLevel = updateLogLevel(config, desc.getLogLevel());
        final var hasUpdatedDeployMode = updateDeployMode(config, desc.getDeployMode());
        final var hasUpdatedTrustStore = updateTrustStore(config, desc.getTruststoreSettings());
        final var hasUpdatedKeyStore = updateKeyStore(config, desc.getKeystoreSettings());
        final var hasUpdatedAdditional = updateAdditional(config, desc.getAdditional());
        final var hasUpdatedProxy = updateProxy(config, desc.getProxySettings());

        return hasUpdatedLogLevel || hasUpdatedDeployMode || hasUpdatedTrustStore || hasUpdatedKeyStore
                || hasUpdatedAdditional || hasUpdatedProxy;
    }

    private boolean updateKeyStore(final Configuration config, final KeystoreDesc desc) {
        // TODO only update if keystore really changed
        config.setKeystore(keystoreFactory.create(desc));
        return true;
    }

    private boolean updateTrustStore(final Configuration config, final TruststoreDesc desc) {
        // TODO only update if truststore really changed
        config.setTruststore(truststoreFactory.create(desc));
        return true;
    }

    private boolean updateDeployMode(final Configuration config,
                                     final ConnectorDeployMode deployMode) {
        // TODO
        config.setDeployMode(Objects.requireNonNullElse(deployMode, ConnectorDeployMode.TEST));
        return true;
    }

    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        // TODO
        config.setLogLevel(Objects.requireNonNullElse(logLevel, LogLevel.OFF));
        return true;
    }

    private boolean updateAdditional(final Configuration configuration,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                configuration.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(configuration::setAdditional);

        return newAdditional.isPresent();
    }

    private boolean updateProxy(final Configuration configuration, final ProxyDesc desc) {
        // TODO only update if proxy really changed
        configuration.setProxy(proxyFactory.create(desc));
        return true;
    }
}
