package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Creates and updates a configuration
 */
@Component
public class ConfigurationFactory implements AbstractFactory<Configuration, ConfigurationDesc> {

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
        final var hasUpdatedDelpoyMode = updateDeployMode(config, config.getDeployMode());
        final var hasUpdatedTrustStore = updateTrustStore(config, config.getTrustStore());
        final var hasUpdatedTrustPassword = updateTrustStorePassword(config, config.getTrustStorePassword());
        final var hasUpdatedKeyStore = updateKeyStore(config, config.getKeyStore());
        final var hasUpdatedKeyStorePassword = updateKeyStorePassword(config, config.getKeyStorePassword());

        return hasUpdatedLogLevel || hasUpdatedDelpoyMode || hasUpdatedTrustStore || hasUpdatedTrustPassword
                || hasUpdatedKeyStore || hasUpdatedKeyStorePassword;
    }

    /**
     * @param config           The entity to be updated.
     * @param keyStorePassword The key store password of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateKeyStorePassword(final Configuration config, final String keyStorePassword) {
        final var newKeystorePassword = MetadataUtils.updateString(config.getKeyStorePassword(),
                keyStorePassword, "password");
        newKeystorePassword.ifPresent(config::setKeyStore);
        return newKeystorePassword.isPresent();
    }

    /**
     * @param config   The entity to be updated.
     * @param keyStore The key store of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateKeyStore(final Configuration config, final String keyStore) {
        final var newKeystore = MetadataUtils.updateString(config.getKeyStore(),
                keyStore, "https://keystore");
        newKeystore.ifPresent(config::setKeyStore);
        return newKeystore.isPresent();
    }

    /**
     * @param config             The entity to be updated.
     * @param trustStorePassword The trust store password of the entity.
     * @return True, if configuration is updated.
     */
    private boolean updateTrustStorePassword(final Configuration config, final String trustStorePassword) {
        final var newPassword = MetadataUtils.updateString(config.getTrustStorePassword(),
                trustStorePassword, "password");
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
                trustStore, "https://truststore");
        newTrustStore.ifPresent(config::setTrustStore);
        return newTrustStore.isPresent();
    }

    /**
     * @param config     The entity to be updated.
     * @param deployMode The deploy mode of the configuration
     * @return True, if configuration is updated.
     */
    private boolean updateDeployMode(final Configuration config, final ConnectorDeployMode deployMode) {
        final boolean updated;
        if (config.getDeployMode().equals(deployMode)) {
            updated = false;
        } else {
            config.setDeployMode(deployMode);
            updated = true;
        }
        return updated;
    }

    /**
     * @param config   The entity to be updated.
     * @param logLevel The log level of the configuration.
     * @return True, if configuration is updated
     */
    private boolean updateLogLevel(final Configuration config, final LogLevel logLevel) {
        final boolean updated;
        if (config.getLogLevel().equals(logLevel)) {
            updated = false;
        } else {
            config.setLogLevel(logLevel);
            updated = true;
        }
        return updated;
    }
}
