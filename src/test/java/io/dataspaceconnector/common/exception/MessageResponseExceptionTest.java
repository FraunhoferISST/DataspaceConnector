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
package io.dataspaceconnector.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageResponseExceptionTest {
    @Test
    public void constructor_someMsg_holdsMsg() {
        /* ARRANGE */
        final var msg = "Some msg";

        /* ACT */
        final var exception = new MessageResponseException(msg);

        /* ASSERT */
        assertEquals(msg, exception.getMessage());
    }

    @Test
    public void constructor_someMsgAndsSomeException_holdsMsgAndException() {
        /* ARRANGE */
        final var msg = "Some msg";
        final var someError = new RuntimeException("WELL?");

        /* ACT */
        final var exception = new MessageResponseException(msg, someError);

        /* ASSERT */
        assertEquals(msg, exception.getMessage());
        assertEquals(someError, exception.getCause());
    }
}
