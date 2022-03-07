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
package io.dataspaceconnector.model.util;

import io.dataspaceconnector.common.ids.policy.PolicyPattern;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains utility methods for updating entity attributes.
 */
public final class FactoryUtils {

    /**
     * Default constructor.
     */
    private FactoryUtils() {
        // not used
    }

    /**
     * Update string.
     *
     * @param oldString     Old string.
     * @param newString     New string.
     * @param defaultString Default value.
     * @return Optional with the new value or without a value.
     */
    public static Optional<String> updateString(final String oldString,
                                                final String newString,
                                                final String defaultString) {
        final var newValue = newString == null ? defaultString : newString;
        if (oldString == null || !oldString.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }

    /**
     * Update integer.
     *
     * @param oldInt Old port.
     * @param newInt New port.
     * @return New port, if new port is different from the old port.
     */
    public static Integer updateInteger(final int oldInt, final int newInt) {
        if (oldInt != newInt) {
            return newInt;
        }

        return oldInt;
    }

    /**
     * Update boolean.
     *
     * @param oldBoolean     Old value.
     * @param newBoolean     New value.
     * @param defaultBoolean Default value.
     * @return Optional with the new value or without a value.
     */
    public static Optional<Boolean> updateBoolean(final Boolean oldBoolean,
                                                  final Boolean newBoolean,
                                                  final Boolean defaultBoolean) {
        final var newValue = newBoolean == null ? defaultBoolean : newBoolean;
        if (oldBoolean == null || !oldBoolean.equals(newValue)) {
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
     * Update list of policy patterns.
     *
     * @param oldList     Old list.
     * @param newList     New list.
     * @param defaultList Default values.
     * @return Optional with the new value or without a value.
     */
    public static Optional<List<PolicyPattern>> updatePolicyList(
            final List<PolicyPattern> oldList,
            final List<PolicyPattern> newList,
            final List<PolicyPattern> defaultList) {
        final var newValues = cleanPolicyList(
                newList == null ? defaultList : newList
        );

        if (oldList == null || !oldList.equals(newValues)) {
            return Optional.of(newValues);
        }

        return Optional.empty();
    }

    /**
     * Update list of uris.
     *
     * @param oldList     Old list.
     * @param newList     New list.
     * @param defaultList Default values.
     * @return Optional with the new value or without a value.
     */
    public static Optional<List<URI>> updateUriList(
            final List<URI> oldList,
            final List<URI> newList,
            final List<URI> defaultList) {
        final var newValues = cleanUriList(newList == null ? defaultList : newList);

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
     * Clean list of uris.
     *
     * @param list List of uris.
     * @return Cleared list.
     */
    public static List<URI> cleanUriList(final List<URI> list) {
        return removeNullFromList(list);
    }

    /**
     * Clean list of policypatterns.
     *
     * @param list List of policypatterns.
     * @return Cleared list.
     */
    public static List<PolicyPattern> cleanPolicyList(final List<PolicyPattern> list) {
        return removeNullFromList(list);
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
