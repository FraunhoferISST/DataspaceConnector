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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Intercepts {@link de.fraunhofer.ids.messaging.core.config.ConfigProducer} and changes how the
 * startup configuration is loaded.
 */
@Component
@Slf4j
@AllArgsConstructor
@Transactional
public class PreConfigInterceptor implements PreConfigProducerInterceptor {

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
        if (doesStoredConfigExits()) {
            return loadConfigFromDb();
        } else {
            return loadConfigFromFile(properties);
        }
    }

    private boolean doesStoredConfigExits() {
        return configurationSvc.findActiveConfig().isPresent();
    }

    private ConfigurationModel loadConfigFromDb() {
        return configModelBuilder.create(configurationSvc.findActiveConfig().get());
    }

    private ConfigurationModel loadConfigFromFile(final ConfigProperties properties)
            throws ConfigProducerInterceptorException {
        try {
            return loadConfig(properties);
        } catch (IOException | ConfigUpdateException e) {
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
            log.info("Importing configuration from file.");
        }

        final var configModel = deserializationSvc.getConfigurationModel(config);
        final var dscConfig
                = configurationSvc.create(FromIdsObjectMapper.fromIdsConfig(configModel));
        configurationSvc.swapActiveConfig(dscConfig.getId());
        return configModel;
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN",
            justification = "path of config json should be specified by user")
    private String getConfiguration(final ConfigProperties properties) throws IOException {
        if (Paths.get(properties.getPath()).isAbsolute()) {
            return getAbsolutePathConfig(properties);
        } else {
            return getClassPathConfig(properties);
        }
    }

    @SuppressFBWarnings(value = {"PATH_TRAVERSAL_IN", "REC_CATCH_EXCEPTION",
            "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
            justification = "path of config json should be specified by user")
    private String getClassPathConfig(final ConfigProperties properties) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Loading config from classpath. [path=({})]",
                    properties.getPath().replaceAll("[\r\n]", "")
            );
        }

        try (var configStream = new ClassPathResource(properties.getPath()).getInputStream()) {
            return new String(configStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not load config from classpath. [path=({})]",
                        properties.getPath().replaceAll("[\r\n]", ""));
                throw new IOException(e.getMessage(), e);
            }
        }
        return "";
    }

    @SuppressFBWarnings(value = {"PATH_TRAVERSAL_IN", "REC_CATCH_EXCEPTION"},
            justification = "path of config json should be specified by user")
    private String getAbsolutePathConfig(final ConfigProperties properties) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Loading config from absolute path. [path=({})]",
                    properties.getPath().replaceAll("[\r\n]", ""));
        }
        try (var fis = new FileInputStream(properties.getPath())) {
            return new String(fis.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not load config from absolute path. [path=({})]",
                        properties.getPath().replaceAll("[\r\n]", ""));
                throw new IOException(e.getMessage(), e);
            }
        }
        return "";
    }

}
