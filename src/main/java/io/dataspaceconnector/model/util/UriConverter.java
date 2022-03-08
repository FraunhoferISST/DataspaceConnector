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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;

/**
 * A converter for converting URIs to strings and strings to URIs. Used for persisting entity
 * attributes of type URI.
 */
@Converter
@NoArgsConstructor
public class UriConverter implements AttributeConverter<URI, String> {

    /**
     * Converts a URI to a string.
     *
     * @param uri the URI.
     * @return string representation of the URI.
     */
    @Override
    public String convertToDatabaseColumn(final URI uri) {
        return uri == null ? null : uri.toString();
    }

    /**
     * Converts a string to a URI.
     *
     * @param string the string.
     * @return URI representation of the string.
     */
    @Override
    public URI convertToEntityAttribute(final String string) {
        return string == null ? null : URI.create(string);
    }

}
