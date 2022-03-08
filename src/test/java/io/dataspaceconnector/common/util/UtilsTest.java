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
package io.dataspaceconnector.common.util;

import io.dataspaceconnector.common.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    /***********************************************************************************************
     * requireNonNull.                                                                             *
     **********************************************************************************************/

    @Test
    public void requireNonNull_nullObj_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var errorMsg = ErrorMessage.DESC_NULL;

        /* ACT && ASSERT */
        final var msg = assertThrows(IllegalArgumentException.class,
                () -> Utils.requireNonNull(null, errorMsg));
        assertEquals(errorMsg.toString(), msg.getMessage());
    }

    @Test
    public void requireNonNull_nullMsg_returnObj() {
        /* ARRANGE */
        final var obj = 5;

        /* ACT */
        final var value = Utils.requireNonNull(obj, null);

        /* ASSERT */
        assertEquals(5, value);
    }

    @Test
    public void requireNonNull_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> Utils.requireNonNull(null, null));
    }

    @Test
    public void requireNonNull_Valid_returnObj() {
        /* ARRANGE */
        final var obj = 5;

        /* ACT */
        final var value = Utils.requireNonNull(obj, ErrorMessage.DESC_NULL);

        /* ASSERT */
        assertEquals(5, value);
    }

    /***********************************************************************************************
     * toStream.                                                                                   *
     **********************************************************************************************/

    @Test
    public void toStream_null_returnEmptyStream() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = Utils.toStream(null);

        /* ASSERT */
        assertEquals(Stream.empty().collect(Collectors.toList()), result.collect(Collectors.toList()));
    }

    @Test
    public void toStream_validCollection_returnStream() {
        /* ARRANGE */
        final var list = List.of(1, 2, 3);

        /* ACT */
        final var result = Utils.toStream(list);

        /* ASSERT */
        assertEquals(list, result.collect(Collectors.toList()));
    }

    /***********************************************************************************************
    * toPage.                                                                                      *
    ***********************************************************************************************/

    @Test
    public void toPage_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> Utils.toPage(null, Pageable.unpaged()));
    }

    @Test
    public void toPage_nullPageable_throwIllegalArgumentException() {
        /* ARRANGE */
        final var list = new ArrayList<Integer>();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> Utils.toPage(list, null));
    }

    @Test
    public void toPage_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> Utils.toPage(null, null));
    }

    @Test
    public void toPage_unpaged_returnAllElements() {
        /* ARRANGE */
        final var list = List.of(5, 4, 3, 2);

        /* ACT */
        final var result = Utils.toPage(list, Pageable.unpaged());

        /* ASSERT */
        assertEquals(list, result.toList());
    }

    @Test
    public void toPage_paged_returnOnlyPage() {
        /* ARRANGE */
        final var list = List.of(5, 4, 3, 2, 1);
        final var pageable = PageRequest.of(1, 2);

        /* ACT */
        final var result = Utils.toPage(list, pageable);

        /* ASSERT */
        assertEquals(2, result.getSize());
        assertTrue(result.toList().contains(2));
        assertTrue(result.toList().contains(3));
    }

    /***********************************************************************************************
     * toPageRequest.                                                                              *
     **********************************************************************************************/

    @Test
    public void toPageRequest_null_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(null, null);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_negativeValues_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(-500, -3);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_sizeBiggerThenMaxSize_PageLimitedToMaxSize() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(Utils.DEFAULT_FIRST_PAGE, Utils.MAX_PAGE_SIZE + 20);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.MAX_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_validValues_returnRequestWithRandomValues() {
        /* ARRANGE */
        final var page = 21;
        final var size = 50;

        /* ACT */
        final var result = Utils.toPageRequest(page, size);

        /* ASSERT */
        assertEquals(PageRequest.of(page, size), result);
    }

    /***********************************************************************************************
     * DEFAULT_PAGE_SIZE.                                                                          *
     **********************************************************************************************/
    @Test
    public void DEFAULT_PAGE_SIZE_IS_30() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(30, Utils.DEFAULT_PAGE_SIZE);
    }

    /***********************************************************************************************
     * DEFAULT_FIRST_PAGE.                                                                         *
     **********************************************************************************************/
    @Test
    public void DEFAULT_FIRST_PAGE_IS_0() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(0, Utils.DEFAULT_FIRST_PAGE);
    }

    /***********************************************************************************************
     * MAX_PAGE_SIZE.                                                                              *
     **********************************************************************************************/
    @Test
    public void MAX_PAGE_SIZE_IS_100() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(100, Utils.MAX_PAGE_SIZE);
    }
}
