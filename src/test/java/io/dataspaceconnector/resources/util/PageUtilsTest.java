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
package io.dataspaceconnector.resources.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageUtilsTest {


    /**************************************************************************
     * toPage.
     *************************************************************************/

    @Test
    public void toPage_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> PageUtils.toPage(null, Pageable.unpaged()));
    }

    @Test
    public void toPage_nullPageable_throwIllegalArgumentException() {
        /* ARRANGE */
        final var list = new ArrayList<Integer>();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> PageUtils.toPage(list, null));
    }

    @Test
    public void toPage_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> PageUtils.toPage(null, null));
    }

    @Test
    public void toPage_unpaged_returnAllElements() {
        /* ARRANGE */
        final var list = List.of(5, 4, 3, 2);

        /* ACT */
        final var result = PageUtils.toPage(list, Pageable.unpaged());

        /* ASSERT */
        assertEquals(list, result.toList());
    }

    @Test
    public void toPage_paged_returnOnlyPage() {
        /* ARRANGE */
        final var list = List.of(5, 4, 3, 2, 1);
        final var pageable = PageRequest.of(1, 2);

        /* ACT */
        final var result = PageUtils.toPage(list, pageable);

        /* ASSERT */
        assertEquals(2, result.getSize());
        assertTrue(result.toList().contains(2));
        assertTrue(result.toList().contains(3));
    }
}
