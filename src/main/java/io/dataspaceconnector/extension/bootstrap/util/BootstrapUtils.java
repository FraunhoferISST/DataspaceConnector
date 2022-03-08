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
package io.dataspaceconnector.extension.bootstrap.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Offers utility functions for bootstrapping data.
 */
@Log4j2
public final class BootstrapUtils {

    /**
     * Some entries in bootstrap property files allow multiple values. This is the delimiter that is
     * used to separate the values.
     */
    private static final String MULTI_VALUE_DELIM = ",";

    /**
     * Set of entries in bootstrap property files that are allowed to have multiple values.
     */
    private static final Set<String> MULTI_VALUE_PROPS
            = new HashSet<>(Arrays.asList("resource.download.auto"));

    private BootstrapUtils() {
        // Nothing to do here.
    }

    /**
     * Collect all bootstrap configuration files and merge them into a single {@link Properties}
     * object. In case of conflicts for values that don't support multiple values, the first one
     * found will be used.
     *
     * @param path      The path of the bootstrapping file.
     * @param name      The (optional) name of the bootstrapping file.
     * @param extension The extension of the bootstrapping file.
     * @return Properties that contain the merged content of all bootstrap config files.
     * @throws FileNotFoundException Could not open properties file.
     */
    public static Properties retrieveBootstrapConfig(final String path,
                                                     final String name,
                                                     final String extension)
            throws FileNotFoundException {
        final var config = new Properties();

        // Iterate all bootstrap.properties files
        for (final var propertyFile : findFilesByExtension(path, name, extension)) {
            try {
                final var properties = new Properties();
                properties.load(FileUtils.openInputStream(propertyFile));

                // Iterate all properties from file and check for duplicates.
                for (final var property : properties.stringPropertyNames()) {
                    loadConfig(config, property, properties.getProperty(property));
                }
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Could not open properties file. [path=({})]",
                            propertyFile.getPath(), e);
                }
            }
        }

        return config;
    }

    /*
     * NOTE: Unwrap the property (key, value) so that we do not typecast Object to String whenever
     * using the property
     */
    private static void loadConfig(final Properties config, final String key, final String value) {
        if (config.containsKey(key)) {
            if (MULTI_VALUE_PROPS.contains(key)) {
                final var multipleValues = Arrays.stream(value.split(MULTI_VALUE_DELIM))
                        .map(String::trim)
                        .collect(Collectors.toSet());
                config.put(key, multipleValues.add((String) config.get(key)));
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Collision for single-value property '{}' found. "
                                    + "Going to keep the old value '{}'; new value '{}' "
                                    + "will be ignored.",
                            key, config.get(key).toString(), value);
                }
            }
        } else {
            if (MULTI_VALUE_PROPS.contains(key)) {
                final var multipleValues = Arrays.stream(value.split(MULTI_VALUE_DELIM))
                        .map(String::trim)
                        .collect(Collectors.toSet());
                config.put(key, multipleValues);
            } else {
                config.put(key, value);
            }
        }
    }

    /**
     * Find all files with given extension in a given path. All files with matching extension will
     * be returned. The search includes subdirectories.
     *
     * @param path      The starting path for searching.
     * @param extension The searched file extension.
     * @return A list of all files that are stored at given path (and subdirectories) with required
     * extension and optional required filename.
     * @throws FileNotFoundException if the given path does not exist.
     */
    public static List<File> findFilesByExtension(final String path, final String extension)
            throws FileNotFoundException {
        return findFilesByExtension(path, null, extension);
    }

    /**
     * Find all files with given extension in a given path. Optionally, a filename can be provided.
     * If filename is set to null, all files with matching extension will be returned. The search
     * includes subdirectories.
     *
     * @param path      The starting path for searching.
     * @param filename  Optional filename that is searched, null for all files.
     * @param extension The searched file extension.
     * @return A list of all files that are stored at given path (and subdirectories) with required
     * extension and optional required filename.
     * @throws FileNotFoundException if the given path does not exist.
     * @throws NullPointerException  if the directory does not contain child files.
     */
    public static List<File> findFilesByExtension(final String path, final String filename,
                                                  final String extension)
            throws FileNotFoundException, NullPointerException {
        // Validate input.
        final var base = io.dataspaceconnector.common.file.FileUtils.openFile(path);
        final var files = new ArrayList<File>();
        if (base.isDirectory()) {
            // If the base file is a directory, iterate all child files.
            for (final var child
                    : io.dataspaceconnector.common.file.FileUtils.getContainedFiles(base)) {
                if (child.isDirectory()) {
                    files.addAll(findFilesByExtension(child.getPath(), filename, extension));
                } else {
                    if (isSearchedFile(child, filename, extension)) {
                        files.add(child);
                    }
                }
            }
        } else {
            // Check if the file matches the search parameters
            if (isSearchedFile(base, filename, extension)) {
                files.add(base);
            }
        }

        return files;
    }

    /**
     * Convert string to url.
     *
     * @param address The recipient's address.
     * @return The address as url.
     */
    public static Optional<URL> toUrl(final String address) {
        try {
            return Optional.of(new URL(address));
        } catch (MalformedURLException ignored) {
            // Nothing to do here.
        }
        return Optional.empty();
    }

    private static boolean isSearchedFile(final File file, final String name, final String ext) {
        return hasExtension(file, ext) && (name == null || doesMatchName(file, name));
    }

    private static boolean hasExtension(final File file, final String ext) {
        return FilenameUtils.getExtension(file.getName()).equals(ext);
    }

    private static boolean doesMatchName(final File file, final String match) {
        return FilenameUtils.removeExtension(file.getName()).equals(match);
    }
}
