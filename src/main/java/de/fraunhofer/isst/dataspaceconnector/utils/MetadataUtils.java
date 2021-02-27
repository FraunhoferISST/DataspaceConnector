package de.fraunhofer.isst.dataspaceconnector.utils;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MetadataUtils {
    private MetadataUtils() {

    }

    public static Optional<String> updateString(final String oldTitle,
                                                final String newTitle,
                                                final String defaultTitle) {
        final var newValue = newTitle == null ? defaultTitle : newTitle;
        if (oldTitle == null || !oldTitle.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

    public static Optional<URI> updateUri(final URI oldUri, final URI newUri,
                                                         final URI defaultUri) {
        final var newValue = newUri == null ? defaultUri : newUri;
        if (oldUri == null || !oldUri.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

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

    public static Optional<Map<String, String>> updateStringMap(final Map<String, String> oldMap,
                                                                final Map<String, String> newMap,
                                                                final Map<String, String> defaultMap) {
        // TODO Implement cleaning like in updateStringList
        final var newValues = newMap == null ? defaultMap : newMap;
        if (oldMap == null || !newMap.equals(newValues)) {
            return Optional.of(newValues);
        }

        return Optional.empty();
    }

    public static List<String> cleanStringList(final List<String> list) {
        var result = removeNullFromList(list);
        result = removeEmptyStringFromList(result);
        return result;
    }

    public static <T> List<T> removeNullFromList(final List<T> list) {
        return list.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<String> removeEmptyStringFromList(final List<String> list) {
        return list.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }
}
