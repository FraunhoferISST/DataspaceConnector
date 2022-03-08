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
package io.dataspaceconnector.model.util;

import java.net.URI;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UriConverterTest {

    private final UriConverter uriConverter = new UriConverter();

    @Test
    public void convertToDatabaseColumn_inputNull_returnNull() {
        /* ACT */
        final var result = uriConverter.convertToDatabaseColumn(null);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void convertToDatabaseColumn_inputNotNull_returnStringRepresentation() {
        /* ARRANGE */
        final var uri = URI.create("https://some-uri.com");

        /* ACT */
        final var result = uriConverter.convertToDatabaseColumn(uri);

        /* ASSERT */
        assertEquals(uri.toString(), result);
    }

    @Test
    public void convertToEntityAttribute_inputNull_returnNull() {
        /* ACT */
        final var result = uriConverter.convertToEntityAttribute(null);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void convertToEntityAttribute_inputNotNull_returnUri() {
        /* ARRANGE */
        final var string = "https://some-uri.com";

        /* ACT */
        final var result = uriConverter.convertToEntityAttribute(string);

        /* ASSERT */
        assertEquals(URI.create(string), result);
    }

}
