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

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URL;

/**
 * A converter for converting URLs to strings and strings to URLs. Used for persisting entity
 * attributes of type URL.
 */
@Converter
@NoArgsConstructor
public class UrlConverter implements AttributeConverter<URL, String> {

    /**
     * Converts a URL to a string.
     *
     * @param url the URL.
     * @return string representation of the URL.
     */
    @Override
    public String convertToDatabaseColumn(final URL url) {
        return url == null ? null : url.toString();
    }

    /**
     * Converts a string to a URI.
     *
     * @param string the string.
     * @return URL representation of the string.
     */
    @Override
    @SneakyThrows
    public URL convertToEntityAttribute(final String string) {
        return string == null ? null : new URL(string);
    }

}
