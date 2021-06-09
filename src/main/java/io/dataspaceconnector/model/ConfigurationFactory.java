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

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Creates and updates a configuration.
 */
@Component
public class ConfigurationFactory implements AbstractFactory<Configuration, ConfigurationDesc> {

    /**
     * Default password.
     */
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Default trust store.
     */
    private static final String DEFAULT_TRUSTSTORE = "https://truststore";

    /**
     * Default trust store.
     */
    private static final String DEFAULT_KEYSTORE = "https://keystore";


    /**
     * @param desc The description of the entity.
     * @return The new configuration entity.
     */
    @Override
    public Configuration create(final ConfigurationDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.MESSAGE_NULL);

        final var config = new Configuration();
        config.setProxy(new ArrayList<>());

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
        Utils.requireNonNull(config, ErrorMessages.MESSAGE_NULL);
        Utils.requireNonNull(desc, ErrorMessages.MESSAGE_NULL);

        final var hasUpdatedLogLevel = updateLogLevel(config, desc.getLogLevel());
        final var hasUpdatedDeployMode = updateDeployMode(config, desc.getDeployMode());
        final var hasUpdatedTrustStore = updateTrustStore(config, desc.getTrustStore());
        final var hasUpdatedTrustPassword = updateTrustStorePassword(config,
                desc.getTrustStorePassword());
        final var hasUpdatedKeyStore = updateKeyStore(config, desc.getKeyStore());
        final var hasUpdatedKeyStorePassword = updateKeyStorePassword(config,
                desc.getKeyStorePassword());
        final var hasUpdatedAdditional = updateAdditional(config, desc.getAdditional());

        return hasUpdatedLogLevel || hasUpdatedDeployMode || hasUpdatedTrustStore
                || hasUpdatedTrustPassword || hasUpdatedKeyStore || hasUpdatedKeyStorePassword
                || hasUpdatedAdditional;
    }

    /**
     * @param config           The entity to be updated.
     * @param keyStorePassword The key store password of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateKeyStorePassword(final Configuration config,
                                           final String keyStorePassword) {
        final var newKeystorePassword =
                MetadataUtils.updateString(config.getKeyStorePassword(), keyStorePassword,
                        DEFAULT_PASSWORD);
        newKeystorePassword.ifPresent(config::setKeyStorePassword);
        return newKeystorePassword.isPresent();
    }

    /**
     * @param config   The entity to be updated.
     * @param keyStore The key store of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateKeyStore(final Configuration config, final String keyStore) {
        final var newKeystore = MetadataUtils.updateString(config.getKeyStore(),
                keyStore, DEFAULT_KEYSTORE);
        newKeystore.ifPresent(config::setKeyStore);
        return newKeystore.isPresent();
    }

    /**
     * @param config             The entity to be updated.
     * @param trustStorePassword The trust store password of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateTrustStorePassword(final Configuration config,
                                             final String trustStorePassword) {
        final var newPassword =
                MetadataUtils.updateString(config.getTrustStorePassword(), trustStorePassword,
                        DEFAULT_PASSWORD);
        newPassword.ifPresent(config::setTrustStorePassword);
        return newPassword.isPresent();
    }

    /**
     * @param config     The entity to be updated.
     * @param trustStore The trust store of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateTrustStore(final Configuration config, final String trustStore) {
        final var newTrustStore = MetadataUtils.updateString(config.getTrustStore(),
                trustStore, DEFAULT_TRUSTSTORE);
        newTrustStore.ifPresent(config::setTrustStore);
        return newTrustStore.isPresent();
    }

    /**
     * @param config     The entity to be updated.
     * @param deployMode The deploy mode of the configuration
     * @return True, if configuration is updated.
     */
    private boolean updateDeployMode(final Configuration config,
                                     final ConnectorDeployMode deployMode) {
        config.setDeployMode(Objects.requireNonNullElse(deployMode, ConnectorDeployMode.TEST));
        return true;
    }

    /**
     * @param config   The entity to be updated.
     * @param logLevel The log level of the configuration.
     * @return True, if configuration is updated
     */
    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        config.setLogLevel(Objects.requireNonNullElse(logLevel, LogLevel.NO));
        return true;
    }

    /**
     * @param configuration The entity to be updated.
     * @param additional    The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final Configuration configuration,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                configuration.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(configuration::setAdditional);

        return newAdditional.isPresent();
    }
}
