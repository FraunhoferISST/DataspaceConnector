package io.dataspaceconnector.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final Set<String> MULTI_VALUE_PROPS = SetUtils.hashSet(
            "resource.download.auto"
    );

    /**
     * Default constructor.
     */
    private BootstrapUtils() {
        // not used
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
     */
    public static List<File> findFilesByExtension(final String path, final String filename,
                                                  final String extension) throws FileNotFoundException, NullPointerException {
        // Validate input.
        final var base = new File(path);
        if (!base.exists()) {
            throw new FileNotFoundException("File '" + path + "' does not exist.");
        }

        final var files = new ArrayList<File>();
        if (base.isDirectory()) {
            // If the base file is a directory, iterate all child files.
            for (final var child : Objects.requireNonNull(base.listFiles())) {
                if (child.isDirectory()) {
                    files.addAll(findFilesByExtension(child.getPath(), filename, extension));
                } else {
                    if (isSearchedFile(child, filename, extension)) {
                        files.add(child);
                    }
                }
            }
        } else {
            // Check if the base file itself is a json-ld file <- TODO: JsonLd?
            if (isSearchedFile(base, filename, extension)) {
                files.add(base);
            }
        }

        return files;
    }

    /**
     * Collect all bootstrap configuration files and merge them into a single {@link Properties}
     * object. In case of conflicts for values that don't support multiple values, the first one
     * found will be used.
     *
     * @param path The path of the bootstrapping file.
     * @param name The (optional) name of the bootstrapping file.
     * @param ext  The extension of the bootstrapping file.
     * @return Properties that contain the merged content of all bootstrap config files.
     */
    public static Properties retrieveBootstrapConfig(final String path,
                                                     final String name,
                                                     final String ext) throws FileNotFoundException {
        final var config = new Properties();

        // Iterate all bootstrap.properties files
        for (final var propertyFile : findFilesByExtension(path, name, ext)) {
            final var properties = new Properties();
            try {
                properties.load(FileUtils.openInputStream(propertyFile));
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Could not open properties file '{}'.", propertyFile.getPath(), e);
                }

                continue;
            }

            // Iterate all properties from file and check for duplicates.
            for (final Map.Entry<Object, Object> property : properties.entrySet()) {
                loadConfig(config, property);
            }
        }

        return config;
    }

    /**
     *
     * @param config
     * @param property
     */
    private static void loadConfig(final Properties config, final Map.Entry<Object, Object> property) {
        if (config.containsKey(property.getKey())) {
            if (MULTI_VALUE_PROPS.contains((String) property.getKey())) {
                final var multipleValues = Arrays.stream(((String) property.getValue())
                        .split(MULTI_VALUE_DELIM)).map(String::trim).collect(Collectors.toSet());
                final var existingValues = toSet(config.get(property.getKey()));
                config.put(property.getKey(), existingValues.addAll(multipleValues));
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Collision for single-value property '{}' found. "
                                    + "Going to keep the old value '{}'; new value '{}' "
                                    + "will be ignored.",
                            property.getKey().toString(),
                            config.get(property.getKey()).toString(),
                            property.getValue().toString());
                }
            }
        } else {
            if (MULTI_VALUE_PROPS.contains((String) property.getKey())) {
                final var multipleValues = Arrays.stream(((String) property.getValue())
                        .split(MULTI_VALUE_DELIM)).map(String::trim).collect(Collectors.toSet());
                config.put(property.getKey(), multipleValues);
            } else {
                config.put(property.getKey(), property.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<String> toSet(final Object obj) {
        return (Set<String>) obj;
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
