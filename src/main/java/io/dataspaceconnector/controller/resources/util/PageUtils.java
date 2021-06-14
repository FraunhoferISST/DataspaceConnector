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
package io.dataspaceconnector.controller.resources.util;

import org.springframework.data.domain.PageRequest;

/**
 * Offers helper functions for working with page requests.
 */
public final class PageUtils {

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

    private PageUtils() {
        // Nothing to do here.
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
}
