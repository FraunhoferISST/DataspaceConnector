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
package io.dataspaceconnector.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.dataspaceconnector.common.exceptions.messages.ErrorMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
