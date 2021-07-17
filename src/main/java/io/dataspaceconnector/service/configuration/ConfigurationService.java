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

import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.repository.ConfigurationRepository;
import io.dataspaceconnector.service.ids.builder.IdsConfigModelBuilder;
import io.dataspaceconnector.service.resource.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for the configuration.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ConfigurationService extends BaseEntityService<Configuration, ConfigurationDesc> {

    /**
     * The current context.
     */
    private final @NonNull ApplicationContext context;

    /**
     * Builds the ids config.
     */
    private final @NonNull IdsConfigModelBuilder configBuilder;

    /**
     * Try too find the active configuration.
     *
     * @return The active configuration if it exists.
     */
    public Optional<Configuration> findActiveConfig() {
        return ((ConfigurationRepository) getRepository()).findActive();
    }

    /**
     * Get the active configuration.
     * @return The active configuration.
     */
    public Configuration getActiveConfig() {
        final var config = findActiveConfig();
        assert config.isPresent();
        return config.get();
    }

    /**
     * Mark a new configuration as active.
     *
     * @param newConfig Id of the new active configuration.
     */
    public void swapActiveConfig(final UUID newConfig) throws ConfigUpdateException {
        var activeConfig = findActiveConfig();

        if (activeConfig.isPresent()) {
            replaceActiveConfig(newConfig, activeConfig.get());
        } else {
            ((ConfigurationRepository) getRepository()).setActive(newConfig);
        }
    }

    private void replaceActiveConfig(final UUID newConfig, final Configuration activeConfig)
            throws ConfigUpdateException {
        if (activeConfig.getId().equals(newConfig)) {
            return;
        }

        swapActiveConfigInDb(newConfig);
        reload(newConfig);
    }

    private void swapActiveConfigInDb(UUID newConfig) {
        final var repo = (ConfigurationRepository) getRepository();
        repo.unsetActive();
        repo.setActive(newConfig);
    }

    private void reload(final UUID newConfig) throws ConfigUpdateException {
        final var configContainer = findConfigContainer();
        if (configContainer.isPresent()) {
            final var configuration = configBuilder.create(getActiveConfig());
            configContainer.get().updateConfiguration(configuration);
            if (log.isInfoEnabled()) {
               log.info("Changing configuration profile [id=({})]", newConfig);
            }
        }
    }

    private Optional<ConfigContainer> findConfigContainer() {
        try {
            return Optional.of(context.getBean(ConfigContainer.class));
        } catch (NoSuchBeanDefinitionException ignored) { }

        return Optional.empty();
    }
}
