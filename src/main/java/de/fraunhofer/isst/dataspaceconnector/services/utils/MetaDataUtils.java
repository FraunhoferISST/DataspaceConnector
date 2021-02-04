package de.fraunhofer.isst.dataspaceconnector.services.utils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MetaDataUtils {
    private MetaDataUtils() {
        
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

    public static Optional<List<String>> updateStringList(
            final List<String> oldList,
            final List<String> newList,
            final List<String> defaultList) {
        final var newValues = newList == null ? defaultList : newList;
        cleanStringList(newValues);

        if (oldList == null || !oldList.equals(newValues)) {
            return Optional.of(newValues);
        }

        return Optional.empty();
    }

    public static void cleanStringList(final List<String> list) {
        removeNullFromList(list);
        removeEmptyStringFromList(list);
    }

    public static <T> void removeNullFromList(final List<T> list) {
        list.removeIf(Objects::isNull);
    }

    public static void removeEmptyStringFromList(final List<String> list) {
        list.removeIf(String::isEmpty);
    }
}
