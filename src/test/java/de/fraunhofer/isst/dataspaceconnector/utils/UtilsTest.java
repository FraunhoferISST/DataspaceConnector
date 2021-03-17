package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ErrorMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTest {
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
}
