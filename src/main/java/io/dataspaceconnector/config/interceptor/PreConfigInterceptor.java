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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Intercepts {@link de.fraunhofer.ids.messaging.core.config.ConfigProducer}
 * and changes how the startup configuration is loaded.
 */
@Component
@Slf4j
@AllArgsConstructor
public class PreConfigInterceptor implements PreConfigProducerInterceptor {

    /**
     * Serializer for parsing configmodel from json-ld.
     */
    private final Serializer serializer;

    @Override
    public ConfigurationModel perform(ConfigProperties properties)
            throws ConfigProducerInterceptorException {
        if (log.isInfoEnabled()) {
            log.info("Intecepting loading of configuration!");
        }
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

    private ConfigurationModel loadConfig(ConfigProperties properties) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Loading configuration from {}", properties.getPath());
        }

        final var config = getConfiguration(properties);

        if (log.isInfoEnabled()) {
            log.info("Importing configuration from file");
        }

        return serializer.deserialize(config, ConfigurationModel.class);
    }

    private String getConfiguration(final ConfigProperties properties) throws IOException {
        if (Paths.get(properties.getPath()).isAbsolute()) {
            return getAbsolutePathConfig(properties);
        } else {
            return getClassPathConfig(properties);
        }
    }

    private String getClassPathConfig(final ConfigProperties properties) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Loading config from classpath: {}", properties.getPath());
        }

        final var configurationStream = new ClassPathResource(
                properties.getPath()
        ).getInputStream();

        final var config = new String(configurationStream.readAllBytes());
        configurationStream.close();

        return config;
    }

    private String getAbsolutePathConfig(final ConfigProperties properties) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Loading config from absolute Path {}", properties.getPath());
        }

        final var fis = new FileInputStream(properties.getPath());
        final var config = new String(fis.readAllBytes());
        fis.close();

        return config;
    }
}
