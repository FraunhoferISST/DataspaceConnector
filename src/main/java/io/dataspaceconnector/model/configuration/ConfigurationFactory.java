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

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a configuration.
 */
@Component
@RequiredArgsConstructor
public class ConfigurationFactory extends AbstractFactory<Configuration, ConfigurationDesc> {

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
        final var hasUpdatedLogLevel = updateLogLevel(config, desc.getLogLevel());
        final var hasUpdatedDeployMode = updateDeployMode(config, desc.getDeployMode());
        final var hasUpdatedTrustStore = updateTrustStore(config, desc.getTruststoreSettings());
        final var hasUpdatedKeyStore = updateKeyStore(config, desc.getKeystoreSettings());
        final var hasUpdatedProxy = updateProxy(config, desc.getProxySettings());

        return hasUpdatedLogLevel
                || hasUpdatedDeployMode
                || hasUpdatedTrustStore
                || hasUpdatedKeyStore
                || hasUpdatedProxy;
    }

    private boolean updateKeyStore(final Configuration config, final KeystoreDesc desc) {
        // TODO only update if keystore really changed
        config.setKeystore(keystoreFactory.create(desc == null ? new KeystoreDesc() : desc));
        return true;
    }

    private boolean updateTrustStore(final Configuration config, final TruststoreDesc desc) {
        // TODO only update if truststore really changed
        config.setTruststore(truststoreFactory.create(desc == null ? new TruststoreDesc() : desc));
        return true;
    }

    private boolean updateDeployMode(final Configuration config,
                                     final DeployMode deployMode) {
        if (deployMode.equals(config.getDeployMode())) {
            return false;
        }

        config.setDeployMode(deployMode);
        return true;
    }

    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        if (config.getLogLevel().equals(logLevel)) {
            return false;
        }

        config.setLogLevel(logLevel);
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
