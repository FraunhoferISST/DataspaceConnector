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
package io.dataspaceconnector.services.resources.util;

import java.util.ArrayList;
import java.util.List;

import io.dataspaceconnector.common.exceptions.messages.ErrorMessages;
import io.dataspaceconnector.common.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Offers helper functions for working with page requests.
 */
public final class PageUtils {

    private PageUtils() {
        // Nothing to do here.
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
        Utils.requireNonNull(list, ErrorMessages.LIST_NULL);
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);

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
}
