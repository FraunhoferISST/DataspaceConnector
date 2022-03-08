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

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnexpectedResponseExceptionTest {

    @Test
    public void new_content_hasContent() {
        /* ARRANGE */
        final var content = new HashMap<String, Object>();
        content.put("Key", "Value");

        final var exception = new UnexpectedResponseException(content);

        /* ACT */
        final var result = exception.getContent();

        /* ASSERT */
        assertEquals(content, result);
    }

    @Test
    public void new_contentAndException_hasContentAndException() {
        /* ARRANGE */
        final var content = new HashMap<String, Object>();
        content.put("Key", "Value");

        final var throwable = new RuntimeException("HELLO");

        final var exception = new UnexpectedResponseException(content, throwable);

        /* ACT */
        final var result = exception.getContent();
        final var cause = exception.getCause();

        /* ASSERT */
        assertEquals(content, result);
        assertEquals(throwable, cause);
    }
}
