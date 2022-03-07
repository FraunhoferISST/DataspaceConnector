/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.config.camel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.file.FileUtils;
import io.dataspaceconnector.config.ConnectorConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * Component that loads Camel routes from XML files located in a directory specified in
 * application.properties at application start.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class XmlRouteLoader {
    /**
     * Unmarshaller for reading route definitions from XML.
     */
    private final @NonNull Unmarshaller unmarshaller;

    /**
     * Loader for adding routes to the camel context.
     */
    private final @NonNull CamelRouteLoader camelRouteLoader;

    /**
     * Resolver for finding classpath resources with paths matching a given pattern.
     */
    private final @NonNull ResourcePatternResolver patternResolver;

    /**
     * Directory where the XML routes are located.
     */
    @Value("${camel.xml-routes.directory:#null}")
    private String directory;

    /**
     * Loads all Camel routes defined in the XML files in the directory specified in
     * application.properties into the Camel context. If any error occurs and the routes cannot be
     * loaded, an IllegalStateException will be thrown, as Camel routes are required for message
     * handling and IDSCP2 communication.
     */
    @PostConstruct
    public void loadRoutes() {
        try {
            Objects.requireNonNull(directory);
            if (log.isDebugEnabled()) {
                log.debug("Loading Camel routes. [path=({})]", directory);
            }
            loadRoutes(directory);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to load Camel routes. [exception=({})].", e.getMessage());
            }
            throw new IllegalStateException("Failed to load Camel routes.", e);
        }
    }

    /**
     * Loads the Camel routes from the specified directory. Loads them as classpath resources, if
     * the directory is prefixed with "classpath", loads them from the file system otherwise.
     *
     * @param directoryPath the directory path.
     * @throws Exception if reading a file, parsing the XML or adding the route fails.
     */
    private void loadRoutes(final String directoryPath) throws Exception {
        if (directoryPath.startsWith("classpath")) {
            loadRoutesFromClasspath(directoryPath);
        } else {
            loadRoutes(FileUtils.openFile(directoryPath));
        }
    }

    private void loadRoutesFromClasspath(final String directoryPath) throws Exception {
        assert directoryPath.startsWith("classpath");
        loadRoutes(patternResolver.getResources(getPatternForPath(directoryPath)));
    }

    /**
     * Loads the Camel routes defined in the given classpath resources.
     *
     * @param files the classpath resources.
     * @throws Exception if reading a file, parsing the XML or adding the route fails.
     */
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            justification = "The redundant nullcheck happens inside called method from spring")
    private void loadRoutes(final Resource[] files) throws Exception {
        for (var file : files) {
            try (var inputStream = file.getInputStream()) {
                loadRoutesFromInputStream(inputStream);
            }
        }
    }

    /**
     * Loads the Camel routes from a file, if it is a single file. If the file is a directory,
     * this method will be called recursively for all files in the directory.
     *
     * @param file the file, which may be a single file or a directory.
     * @throws Exception if reading a file, parsing the XML or adding the route fails.
     */
    private void loadRoutes(final File file) throws Exception {
        Objects.requireNonNull(file);

        if (file.isDirectory()) {
            for (var subFile : FileUtils.getContainedFiles(file)) {
                loadRoutes(subFile);
            }
        } else {
            try (var inputStream = new FileInputStream(file)) {
                loadRoutesFromInputStream(inputStream);
            }
        }
    }

    /**
     * Loads the content of the specified input stream. The content is parsed to a Camel
     * {@link RoutesDefinition} and added to the Camel context.
     *
     * @param inputStream the input stream of the XML file.
     * @throws Exception if reading the file, parsing the XML or adding the route fails.
     */
    private void loadRoutesFromInputStream(final InputStream inputStream) throws Exception {
        try {
            camelRouteLoader.addRouteToContext(toRoutesDef(inputStream).getRoutes());
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to read route files. [exception=({})]", e.getMessage());
            }
            throw e;
        } catch (JAXBException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to parse route files. [exception=({})]", e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to add routes to context. [exception=({})]", e.getMessage());
            }
            throw e;
        }
    }

    private RoutesDefinition toRoutesDef(final InputStream stream) throws JAXBException {
        return (RoutesDefinition) unmarshaller.unmarshal(stream);
    }

    /**
     * Adds routes to the current camel context.
     */
    @Component
    @RequiredArgsConstructor
    private static class CamelRouteLoader {
        /**
         * The Camel context.
         */
        private final @NonNull CamelContext context;

        /**
         * The current connector configuration.
         */
        private final @NonNull ConnectorConfig connectorConfig;

        public void addRouteToContext(final List<RouteDefinition> routes) throws Exception {
            for (final var route : routes) {
                addRouteToContext(route);
            }
        }

        public void addRouteToContext(final RouteDefinition route) throws Exception {
            if ("idscp2Server".equals(route.getRouteId())) {
                if (connectorConfig.isIdscpEnabled()) {
                    addToContext(route);
                }
            } else {
                addToContext(route);
            }
        }

        private void addToContext(final RouteDefinition route) throws Exception {
            context.adapt(ModelCamelContext.class).addRouteDefinition(route);

            if (log.isDebugEnabled()) {
                log.debug("Loaded route from XML file. [routeId=({})]", route.getRouteId());
            }
        }
    }

    /**
     * Creates the pattern used for finding all XML files under a given directory by appending
     * wildcards for subdirectories and file names.
     *
     * @param path the path to the directory.
     * @return the pattern for finding all XML files in the specified directory.
     */
    private String getPatternForPath(final String path) {
        return path.endsWith("/") ? path.concat("**/*.xml") : path.concat("/**/*.xml");
    }
}
