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

import java.net.MalformedURLException;
import java.net.URL;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlConverterTest {

    private final UrlConverter urlConverter = new UrlConverter();

    @Test
    public void convertToDatabaseColumn_inputNull_returnNull() {
        /* ACT */
        final var result = urlConverter.convertToDatabaseColumn(null);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    @SneakyThrows
    public void convertToDatabaseColumn_inputNotNull_returnStringRepresentation() {
        /* ARRANGE */
        final var url = new URL("https://some-uri.com");

        /* ACT */
        final var result = urlConverter.convertToDatabaseColumn(url);

        /* ASSERT */
        assertEquals(url.toString(), result);
    }

    @Test
    public void convertToEntityAttribute_inputNull_returnNull() {
        /* ACT */
        final var result = urlConverter.convertToEntityAttribute(null);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    @SneakyThrows
    public void convertToEntityAttribute_inputNotNull_returnUrl() {
        /* ARRANGE */
        final var string = "https://some-uri.com";

        /* ACT */
        final var result = urlConverter.convertToEntityAttribute(string);

        /* ASSERT */
        assertEquals(new URL(string), result);
    }

    @Test
    public void convertToEntityAttribute_inputInvalid_throwMalformedURLException() {
        /* ARRANGE */
        final var string = "not a url";

        /* ACT && ASSERT */
        assertThrows(MalformedURLException.class,
                () -> urlConverter.convertToEntityAttribute(string));
    }

}
