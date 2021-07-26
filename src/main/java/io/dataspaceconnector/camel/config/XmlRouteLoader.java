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
package io.dataspaceconnector.camel.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import io.dataspaceconnector.config.ConnectorConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Component that loads Camel routes from XML files located in a directory specified in
 * application.properties at application start.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class XmlRouteLoader {

    /**
     * The Camel context.
     */
    private final @NonNull DefaultCamelContext context;

    /**
     * Unmarshaller for reading route definitions from XML.
     */
    private final @NonNull Unmarshaller unmarshaller;

    /**
     * The current connector configuration.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Resolver for finding classpath resources with paths matching a given pattern.
     */
    private final @NonNull ResourcePatternResolver patternResolver;

    /**
     * The current application context.
     */
    private final @NonNull ApplicationContext applicationContext;

    /**
     * Directory where the XML routes are located.
     */
    @Value("${camel.xml-routes.directory:#null}")
    private String directory;

    /**
     * Loads all Camel routes defined in the XML files in the directory specified in
     * application.properties into the Camel context. If any error occurs and the routes cannot be
     * loaded, the application will be shut down, as Camel routes are required for message handling
     * and IDSCP2 communication.
     */
    @PostConstruct
    public void loadRoutes() {
        try {
            Objects.requireNonNull(directory);
            log.debug("Loading Camel routes from: {}", directory);
            loadRoutes(directory);
        } catch (Exception exception) {
            log.error("Failed to load Camel routes. [exception=({})] Closing application...",
                    exception.getMessage());
            ((ConfigurableApplicationContext) applicationContext).close();
            System.exit(1);
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
            final var pattern = getPatternForPath(directoryPath);
            final var files = patternResolver.getResources(pattern);
            loadRoutes(files);
        } else {
            final var file = new File(directoryPath);
            if (!file.exists()) {
                throw new FileNotFoundException("Unable to find specified path: " + directoryPath);
            }
            loadRoutes(file);
        }
    }

    /**
     * Loads the Camel routes defined in the given classpath resources.
     *
     * @param files the classpath resources.
     * @throws Exception if reading a file, parsing the XML or adding the route fails.
     */
    private void loadRoutes(final Resource[] files) throws Exception {
        for (var file: files) {
            final var inputStream = file.getInputStream();
            loadRoutesFromInputStream(inputStream);
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
            final var subFiles = file.listFiles(new XmlAndDirectoryFilter());
            Objects.requireNonNull(subFiles);
            for (var subFile : subFiles) {
                loadRoutes(subFile);
            }
        } else {
            final var inputStream = new FileInputStream(file);
            loadRoutesFromInputStream(inputStream);
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
            var routes = (RoutesDefinition) unmarshaller.unmarshal(inputStream);

            for (var route: routes.getRoutes()) {
                if ("idscp2Server".equals(route.getRouteId())) {
                    if (connectorConfig.isIdscpEnabled()) {
                        context.addRouteDefinition(route);
                        log.debug("Loaded additional route from XML file: {}", route.getRouteId());
                    }
                } else {
                    context.addRouteDefinition(route);
                    log.debug("Loaded additional route from XML file: {}", route.getRouteId());
                }
            }

            inputStream.close();
        } catch (IOException exception) {
            log.error("Failed to read route files. [exception=({})]", exception.getMessage());
            throw exception;
        } catch (JAXBException exception) {
            log.error("Failed to parse route files. [exception=({})]", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("Failed to add routes to context. [exception=({})]", exception.getMessage());
            throw exception;
        }
    }

    /**
     * Creates the pattern used for finding all XML files under a given directory by appending
     * wildcards for sub directories and file names.
     *
     * @param path the path to the directory.
     * @return the pattern for finding all XML files in the specified directory.
     */
    private String getPatternForPath(final String path) {
        return path.endsWith("/") ? path.concat("**/*.xml") : path.concat("/**/*.xml");
    }

    /**
     * FileFilter implementation that finds all files that are either a directory or an XML file.
     */
    static class XmlAndDirectoryFilter implements FileFilter {
        @Override
        public boolean accept(final File pathname) {
            if (pathname.isDirectory()) {
                return true;
            } else {
                return pathname.getName().toLowerCase().endsWith("xml");
            }
        }
    }
}
