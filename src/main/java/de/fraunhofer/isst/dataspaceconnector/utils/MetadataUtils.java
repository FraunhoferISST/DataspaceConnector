package de.fraunhofer.isst.dataspaceconnector.utils;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MetadataUtils {
    private MetadataUtils() {
        // not used
    }

    /**
     * Update string.
     *
     * @param oldTitle     Old string.
     * @param newTitle     New string.
     * @param defaultTitle Default value.
     * @return Optional with the new value or without a value.
     */
    public static Optional<String> updateString(final String oldTitle,
                                                final String newTitle,
                                                final String defaultTitle) {
        final var newValue = newTitle == null ? defaultTitle : newTitle;
        if (oldTitle == null || !oldTitle.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

    /**
     * Update uri.
     *
     * @param oldUri     Old uri.
     * @param newUri     New uri.
     * @param defaultUri Default value.
     * @return Optional with the new value or without a value.
     */
    public static Optional<URI> updateUri(final URI oldUri, final URI newUri,
                                          final URI defaultUri) {
        final var newValue = newUri == null ? defaultUri : newUri;
        if (oldUri == null || !oldUri.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

    /**
     * Update date.
     *
     * @param oldDate     Old date.
     * @param newDate     New date.
     * @param defaultDate Default value.
     * @return Optional with the new value or without a value.
     */
    public static Optional<ZonedDateTime> updateDate(final ZonedDateTime oldDate,
                                                     final ZonedDateTime newDate,
                                                     final ZonedDateTime defaultDate) {
        final var newValue = newDate == null ? defaultDate : newDate;
        if (oldDate == null || !oldDate.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

    /**
     * Update list of strings.
     *
     * @param oldList     Old list.
     * @param newList     New list.
     * @param defaultList Default values.
     * @return Optional with the new value or without a value.
     */
    public static Optional<List<String>> updateStringList(
            final List<String> oldList,
            final List<String> newList,
            final List<String> defaultList) {
        final var newValues = cleanStringList(newList == null ? defaultList : newList);

        if (oldList == null || !oldList.equals(newValues)) {
            return Optional.of(newValues);
        }

        return Optional.empty();
    }

    /**
     * Update map of strings.
     *
     * @param oldMap     Old map.
     * @param newMap     New map.
     * @param defaultMap Default values.
     * @return Optional with the new value or without a value.
     */
    public static Optional<Map<String, String>> updateStringMap(
            final Map<String, String> oldMap, final Map<String, String> newMap,
            final Map<String, String> defaultMap) {
        // TODO Implement cleaning like in updateStringList
        final var newValues = newMap == null ? defaultMap : newMap;
        if (oldMap == null || !oldMap.equals(newValues)) {
            return Optional.of(newValues);
        }

        return Optional.empty();
    }

    /**
     * Clean list of strings.
     *
     * @param list List of strings.
     * @return Cleared list.
     */
    public static List<String> cleanStringList(final List<String> list) {
        var result = removeNullFromList(list);
        result = removeEmptyStringFromList(result);
        return result;
    }

    /**
     * Remove null values from list.
     *
     * @param list List of values.
     * @param <T>  Class type.
     * @return List without null values.
     */
    public static <T> List<T> removeNullFromList(final List<T> list) {
        return list.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Remove empty values from list of strings.
     *
     * @param list List of strings.
     * @return List without empty strings.
     */
    public static List<String> removeEmptyStringFromList(final List<String> list) {
        return list.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }
}
