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
package io.dataspaceconnector.config;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.ids.messaging.core.config.ConfigProducerInterceptorException;
import de.fraunhofer.ids.messaging.core.config.ConfigProperties;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import de.fraunhofer.ids.messaging.core.config.PreConfigProducerInterceptor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.mapping.FromIdsObjectMapper;
import io.dataspaceconnector.service.resource.ids.builder.IdsConfigModelBuilder;
import io.dataspaceconnector.service.resource.type.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Intercepts {@link de.fraunhofer.ids.messaging.core.config.ConfigProducer} and changes how the
 * startup configuration is loaded.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PreConfigInterceptor implements PreConfigProducerInterceptor {

    /**
     * The property for forcing to reload the configuration from the config.json.
     */
    @Value("${configuration.force.reload:false}")
    private boolean forceReload;

    /**
     * Service for ids deserialization.
     */
    private final DeserializationService deserializationSvc;

    /**
     * Service for configuration management.
     */
    private final ConfigurationService configurationSvc;

    /**
     * Service for mapping ids configModel to dsc configuration.
     */
    private final IdsConfigModelBuilder configModelBuilder;

    /**
     * Loads the connector configuration from the database, if it exists, and from the config.json
     * file otherwise.
     *
     * @param properties the configuration properties.
     * @return the loaded config.
     * @throws ConfigProducerInterceptorException if loading the config fails.
     */
    @Override
    public ConfigurationModel perform(final ConfigProperties properties)
            throws ConfigProducerInterceptorException {
        if (doesStoredConfigExits() && !forceReload) {
            if (log.isInfoEnabled()) {
                log.info("Loading configuration from DB.");
            }
            return loadConfigFromDb();
        } else {
            if (forceReload && log.isInfoEnabled()) {
                log.info("Forced loading configuration from file.");
            }
            return loadConfigFromFile(properties);
        }
    }

    private boolean doesStoredConfigExits() {
        return configurationSvc.findActiveConfig().isPresent();
    }

    private ConfigurationModel loadConfigFromDb() throws ConfigProducerInterceptorException {
        var activeConfig = configurationSvc.findActiveConfig().get();
        var configModel = configModelBuilder.create(activeConfig);
        try {
            configurationSvc.swapActiveConfig(activeConfig.getId(), true);
        } catch (ConfigUpdateException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to load config from database.");
            }
            throw new ConfigProducerInterceptorException(e.getMessage());
        }
        return configModel;
    }

    private ConfigurationModel loadConfigFromFile(final ConfigProperties properties)
            throws ConfigProducerInterceptorException {
        try {
            return loadConfig(properties);
        } catch (IOException | ConfigUpdateException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to load config from file.");
            }
            throw new ConfigProducerInterceptorException(e.getMessage());
        }
    }

    private ConfigurationModel loadConfig(final ConfigProperties properties)
            throws IOException, ConfigUpdateException {
        if (log.isDebugEnabled()) {
            log.debug("Loading configuration. [path=({})] ",
                    properties.getPath().replaceAll("[\r\n]", ""));
        }

        final var config = getConfiguration(properties);
        if (log.isInfoEnabled()) {
            log.info("Loading configuration from file.");
        }

        final var configModel = deserializationSvc.getConfigurationModel(config);

        //copy config values from application.properties as not part of config.json
        configModel.setKeyStorePassword(properties.getKeyStorePassword());
        configModel.setTrustStorePassword(properties.getTrustStorePassword());
        configModel.setKeyStoreAlias(properties.getKeyAlias());
        configModel.setTrustStoreAlias(properties.getKeyAlias());

        final var dscConfig
                = configurationSvc.create(FromIdsObjectMapper.fromIdsConfig(configModel));
        configurationSvc.swapActiveConfig(dscConfig.getId(), true);
        return configModel;
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "Path should be set by user.")
    private String getConfiguration(final ConfigProperties properties) {
        if (Paths.get(properties.getPath()).isAbsolute()) {
            return getAbsolutePathConfig(properties);
        } else {
            return getClassPathConfig(properties);
        }
    }

    @SuppressFBWarnings(value = { "PATH_TRAVERSAL_IN", "REC_CATCH_EXCEPTION" },
            justification = "Path should be set by user.")
    private String getClassPathConfig(final ConfigProperties properties) {
        if (log.isInfoEnabled()) {
            log.info("Loading config from classpath. [path=({})]",
                    properties.getPath().replaceAll("[\r\n]", ""));
        }

        try (var configStream = new ClassPathResource(properties.getPath()).getInputStream()) {
            return new String(configStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not load config from classpath. [path=({})]",
                        properties.getPath().replaceAll("[\r\n]", ""));
            }
        }
        return "";
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "Path should be set by user.")
    private String getAbsolutePathConfig(final ConfigProperties properties) {
        if (log.isInfoEnabled()) {
            log.info("Loading config from absolute path. [path=({})]",
                    properties.getPath().replaceAll("[\r\n]", ""));
        }

        try (var fis = new FileInputStream(properties.getPath())) {
            return new String(fis.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not load config from absolute path. [path=({})]",
                        properties.getPath().replaceAll("[\r\n]", ""));
            }
        }
        return "";
    }

}
