package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
     * toStream.
     *************************************************************************/

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

    /**************************************************************************
     * toSort.
     *************************************************************************/

    @Test
    public void toSort_null_returnUnsorted() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = Utils.toSort(null);

        /* ASSERT */
        assertEquals(Sort.unsorted(), result);
    }

    @Test
    public void toSort_onlyOneProperty_returnSort() {
        /* ARRANGE */
        final var input = "title";

        /* ACT */
        final var result = Utils.toSort(input);

        /* ASSERT */
        assertNotNull(result.getOrderFor(input));
        assertEquals(Sort.Direction.ASC,result.getOrderFor(input).getDirection());
    }

    @Test
    public void toSort_onePropertyAsc_returnAscSort() {
        /* ARRANGE */
        final var input = "title,asc";

        /* ACT */
        final var result = Utils.toSort(input);

        /* ASSERT */
        assertNotNull(result.getOrderFor("title"));
        assertEquals(Sort.Direction.ASC,result.getOrderFor("title").getDirection());
    }

    @Test
    public void toSort_onePropertyDesc_returnDescSort() {
        /* ARRANGE */
        final var input = "title,desc";

        /* ACT */
        final var result = Utils.toSort(input);

        /* ASSERT */
        assertNotNull(result.getOrderFor("title"));
        assertEquals(Sort.Direction.DESC,result.getOrderFor("title").getDirection());
    }

    /**************************************************************************
     * toPageRequest.
     *************************************************************************/

    @Test
    public void toPageRequest_null_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(null, null, null);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_negativeValues_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(-500, -3, null);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_sizeBiggerThenMaxSize_PageLimitedToMaxSize() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(Utils.DEFAULT_FIRST_PAGE, Utils.MAX_PAGE_SIZE + 20, null);

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.MAX_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_invalidSort_defaultRequest() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = Utils.toPageRequest(-500, -3, "INVALID SORT,,");

        /* ASSERT */
        assertEquals(PageRequest.of(Utils.DEFAULT_FIRST_PAGE, Utils.DEFAULT_PAGE_SIZE), result);
    }

    @Test
    public void toPageRequest_validValues_returnRequestWithRandomValues() {
        /* ARRANGE */
        final var page = 21;
        final var size = 50;
        final var sort = "title,desc";

        /* ACT */
        final var result = Utils.toPageRequest(page, size, sort);

        /* ASSERT */
        assertEquals(PageRequest.of(page, size, Sort.by("title").descending()), result);
    }

    /**************************************************************************
     * DEFAULT_PAGE_SIZE.
     *************************************************************************/
    @Test
    public void DEFAULT_PAGE_SIZE_IS_30() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(30, Utils.DEFAULT_PAGE_SIZE);
    }

    /**************************************************************************
     * DEFAULT_FIRST_PAGE.
     *************************************************************************/
    @Test
    public void DEFAULT_FIRST_PAGE_IS_0() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(0, Utils.DEFAULT_FIRST_PAGE);
    }

    /**************************************************************************
     * MAX_PAGE_SIZE.
     *************************************************************************/
    @Test
    public void MAX_PAGE_SIZE_IS_100() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(100, Utils.MAX_PAGE_SIZE);
    }
}
