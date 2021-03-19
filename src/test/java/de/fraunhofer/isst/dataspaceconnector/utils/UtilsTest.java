package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    /**************************************************************************
     * requireNonNull.
     *************************************************************************/

    @Test
    public void requireNonNull_nullObj_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var errorMsg = ErrorMessages.DESC_NULL;

        /* ACT && ASSERT */
        final var msg = assertThrows(
                IllegalArgumentException.class, () -> Utils.requireNonNull(null, errorMsg));
        assertEquals(errorMsg.toString(), msg.getMessage());
    }

    @Test
    public void requireNonNull_nullMsg_returnObj() {
        /* ARRANGE */
        Integer obj = 5;

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
        Integer obj = 5;

        /* ACT */
        final var value = Utils.requireNonNull(obj, ErrorMessages.DESC_NULL);

        /* ASSERT */
        assertEquals(5, value);
    }
    
    /**************************************************************************
    * toPage.
    *************************************************************************/

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
}
