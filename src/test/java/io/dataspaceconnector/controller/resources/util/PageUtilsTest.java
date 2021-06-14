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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageUtilsTest {

    /**************************************************************************
     * toPageRequest.
     *************************************************************************/

    @Test
    public void toPageRequest_null_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = PageUtils.toPageRequest(null, null);

        /* ASSERT */
        assertEquals(PageRequest.of(PageUtils.DEFAULT_FIRST_PAGE, PageUtils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_negativeValues_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = PageUtils.toPageRequest(-500, -3);

        /* ASSERT */
        assertEquals(PageRequest.of(PageUtils.DEFAULT_FIRST_PAGE, PageUtils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_sizeBiggerThenMaxSize_PageLimitedToMaxSize() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = PageUtils.toPageRequest(PageUtils.DEFAULT_FIRST_PAGE, PageUtils.MAX_PAGE_SIZE + 20);

        /* ASSERT */
        assertEquals(PageRequest.of(PageUtils.DEFAULT_FIRST_PAGE, PageUtils.MAX_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_validValues_returnRequestWithRandomValues() {
        /* ARRANGE */
        final var page = 21;
        final var size = 50;

        /* ACT */
        final var result = PageUtils.toPageRequest(page, size);

        /* ASSERT */
        assertEquals(PageRequest.of(page, size), result);
    }

    /**************************************************************************
     * DEFAULT_PAGE_SIZE.
     *************************************************************************/
    @Test
    public void DEFAULT_PAGE_SIZE_IS_30() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(30, PageUtils.DEFAULT_PAGE_SIZE);
    }

    /**************************************************************************
     * DEFAULT_FIRST_PAGE.
     *************************************************************************/
    @Test
    public void DEFAULT_FIRST_PAGE_IS_0() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(0, PageUtils.DEFAULT_FIRST_PAGE);
    }

    /**************************************************************************
     * MAX_PAGE_SIZE.
     *************************************************************************/
    @Test
    public void MAX_PAGE_SIZE_IS_100() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(100, PageUtils.MAX_PAGE_SIZE);
    }
}
