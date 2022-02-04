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
package io.dataspaceconnector.common.util;

import io.dataspaceconnector.common.exception.ErrorMessage;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * This utility class contains general purpose functions.
 */
public final class Utils {

    /**
     * Default page size.
     */
    public static final int DEFAULT_PAGE_SIZE = 30;

    /**
     * Max page size.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Default first page.
     */
    public static final int DEFAULT_FIRST_PAGE = 0;

    /**
     * Default constructor.
     */
    private Utils() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Check if an object is not null.
     *
     * @param obj     The object to check.
     * @param message The error message transmitted when the object is null.
     * @param <T>     The type of the passed object.
     * @return The passed object.
     * @throws IllegalArgumentException if the passed object is null.
     */
    public static <T> T requireNonNull(final T obj, final ErrorMessage message) {
        if (obj == null) {
            throw new IllegalArgumentException(message.toString());
        }

        return obj;
    }

    /**
     * Convert a collection that may be null safely to a stream.
     * If the collection is null an empty stream will be produced.
     *
     * @param collection The collection. May be null.
     * @param <T>        The type of the elements in the collection.
     * @return The stream over the elements of the collection.
     */
    public static <T> Stream<T> toStream(final Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }

    /**
     * Get a page from a list.
     *
     * @param list     The list the page  should be constructed from.
     * @param pageable The page information.
     * @param <T>      The type of the list elements.
     * @return The new page.
     */
    public static <T> Page<T> toPage(final List<T> list, final Pageable pageable) {
        Utils.requireNonNull(list, ErrorMessage.LIST_NULL);
        Utils.requireNonNull(pageable, ErrorMessage.PAGEABLE_NULL);

        if (pageable.equals(Pageable.unpaged())) {
            // All elements should be returned.
            return new PageImpl<>(list, pageable, list.size());
        }

        final var start = (int) pageable.getOffset();

        if (start > pageable.getPageSize()) {
            // There are no more list elements.
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }

        final var end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Check if a parameter is empty.
     *
     * @param <T>   Type of input parameter.
     * @param param The parameter.
     * @return True if it is empty, false if not.
     */
    public static <T> boolean isEmptyOrNull(final T param) {
        if (param == null) {
            return true;
        } else {
            return param.toString().isEmpty();
        }
    }

    /**
     * Creates a page request based on page, size, and sort inputs.
     *
     * @param page The page index.
     * @param size The page size.
     * @return The page request.
     */
    @SuppressWarnings("PMD.UselessParentheses")
    public static PageRequest toPageRequest(final Integer page, final Integer size) {
        final int pageIndex = (page != null && page > 0) ? page : DEFAULT_FIRST_PAGE;
        final int sizeValue = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        return PageRequest.of(pageIndex, sizeValue);
    }

    /**
     * Compare two lists to each other.
     *
     * @param lList   One list.
     * @param rList   The other list.
     * @param compare The function that should be used for comparison.
     * @param <T>     Type of the list.
     * @return True if lists are equal, false if not.
     */
    public static <T> boolean compareList(final List<? extends T> lList,
                                          final List<? extends T> rList,
                                          final BiFunction<T, T, Boolean> compare) {
        var isSame = true;

        if (isOnlyOneNull(lList, rList)) {
            final var cond1 = lList == null && rList.isEmpty();
            final var cond2 = rList == null && lList.isEmpty();
            if (!cond1 && !cond2) {
                isSame = false;
            }
        } else if (lList != null /* && rList != null*/) {
            final var lSet = makeUnique(lList, compare);
            final var rSet = makeUnique(rList, compare);

            if (lSet.size() == rSet.size()) {
                for (final var lObj : lSet) {
                    var found = false;
                    for (final var rObj : rSet) {
                        if (compare.apply(lObj, rObj)) {
                            final var lType = lObj.getClass();
                            final var rType = rObj.getClass();

                            if (lType.equals(rType)) {
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        // At least one element is different
                        isSame = false;
                        break;
                    }
                }
            } else {
                // Two unique sets with different length must have different elements
                isSame = false;
            }
        }

        return isSame;
    }

    private static <T> List<? extends T> makeUnique(final List<? extends T> list,
                                                    final BiFunction<T, T, Boolean> compare) {
        final var output = new ArrayList<>(list);
        for (int x = 0; x < output.size(); x++) {
            final var obj = output.get(x);
            for (int y = x + 1; y < output.size(); y++) {
                if (compare.apply(obj, output.get(y))) {
                    output.remove(y);
                    --y;
                }
            }
        }

        return output;
    }

    @SuppressWarnings("PMD.UselessParentheses")
    private static <T> boolean isOnlyOneNull(final T obj1, final T obj2) {
        return (obj1 == null && obj2 != null) || (obj1 != null && obj2 == null);
    }

    /**
     * Escapes a string to be valid XML.
     *
     * @param input the input string.
     * @return the escaped string.
     */
    public static String escapeForXml(final String input) {
        return StringEscapeUtils.escapeXml11(StringEscapeUtils.unescapeXml(input));
    }
}
