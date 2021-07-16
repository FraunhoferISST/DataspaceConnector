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
package io.dataspaceconnector.config.interceptor;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.ids.messaging.core.config.ConfigProducerInterceptorException;
import de.fraunhofer.ids.messaging.core.config.ConfigProperties;
import de.fraunhofer.ids.messaging.core.config.PreConfigProducerInterceptor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.service.configuration.ConfigurationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static io.dataspaceconnector.config.interceptor.ConfigurationMapper.buildConfigDesc;
import static io.dataspaceconnector.config.interceptor.ConfigurationMapper.buildInfomodelConfig;

/**
 * Intercepts {@link de.fraunhofer.ids.messaging.core.config.ConfigProducer}
 * and changes how the startup configuration is loaded.
 */
@Component
@Slf4j
@AllArgsConstructor
public final class PreConfigInterceptor implements PreConfigProducerInterceptor {

    /**
     * Serializer for parsing configmodel from json-ld.
     */
    private final Serializer serializer;
    /**
     * ConfigurationService, to read and write current configurations from/to DB.
     */
    private final ConfigurationService configurationService;

    @Override
    public ConfigurationModel perform(final ConfigProperties properties)
            throws ConfigProducerInterceptorException {
        if (log.isInfoEnabled()) {
            log.info("Intecepting loading of configuration!");
        }
        var configList = configurationService.findSelected();
        if (!configList.isEmpty()) {
            //there are configurations written in the DB
            if (configList.size() > 1) {
                throw new ConfigProducerInterceptorException(
                        String.format(
                                "There are configurations in the DB,"
                                        + " but %d are marked as selected!",
                                configList.size()
                        )
                );
            } else {
                var selectedConfig = configurationService.get(configList.get(0));
                return buildInfomodelConfig(selectedConfig);
            }
        } else {
            //no config in DB, load from json
            try {
                //TODO check if configmodel is already saved in db,
                // then load from there instead of config file.
                var config = loadConfig(properties);
                config.setProperty("preInterceptor", true);
                return config;
            } catch (IOException e) {
                throw new ConfigProducerInterceptorException(e.getMessage());
            }
        }
    }

    private ConfigurationModel loadConfig(final ConfigProperties properties)
            throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Loading configuration from {}",
                    properties.getPath().replaceAll("[\r\n]", "")
            );
        }

        final var config = getConfiguration(properties);

        if (log.isInfoEnabled()) {
            log.info("Importing configuration from file");
        }

        var infomodelConfig = serializer.deserialize(
                config,
                ConfigurationModel.class
        );
        var dscConfig = buildConfigDesc(infomodelConfig);
        configurationService.create(dscConfig);
        return infomodelConfig;
    }

    @SuppressFBWarnings(
            value = "PATH_TRAVERSAL_IN",
            justification = "path of config json should be specified by user"
    )
    private String getConfiguration(final ConfigProperties properties)
            throws IOException {
        if (Paths.get(properties.getPath()).isAbsolute()) {
            return getAbsolutePathConfig(properties);
        } else {
            return getClassPathConfig(properties);
        }
    }

    @SuppressFBWarnings(
            value = {
                    "PATH_TRAVERSAL_IN",
                    "REC_CATCH_EXCEPTION",
                    "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"
            },
            justification = "path of config json should be specified by user"
    )
    private String getClassPathConfig(final ConfigProperties properties)
            throws IOException {
        if (log.isInfoEnabled()) {
            log.info(
                    "Loading config from classpath: {}",
                    properties.getPath().replaceAll("[\r\n]", "")
            );
        }

        try (var configurationStream = new ClassPathResource(
                properties.getPath()
        ).getInputStream()) {
            return new String(
                    configurationStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "Could not load config from path {}",
                        properties.getPath().replaceAll("[\r\n]", "")
                );
                throw new IOException(e.getMessage(), e);
            }
        }
        return "";
    }

    @SuppressFBWarnings(
            value = {"PATH_TRAVERSAL_IN", "REC_CATCH_EXCEPTION"},
            justification = "path of config json should be specified by user"
    )
    private String getAbsolutePathConfig(final ConfigProperties properties)
            throws IOException {
        if (log.isInfoEnabled()) {
            log.info(
                    "Loading config from absolute Path {}",
                    properties.getPath().replaceAll("[\r\n]", "")
            );
        }
        try (var fis = new FileInputStream(properties.getPath())) {
            return new String(
                    fis.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "Could not load config from absolute path {}",
                        properties.getPath().replaceAll("[\r\n]", "")
                );
                throw new IOException(e.getMessage(), e);
            }
        }
        return "";
    }

}
