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
package io.dataspaceconnector.model.utils;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter for converting list of uris to string or to convert a string to a list of uris.
 */
@Converter
public class ListUriConverter implements AttributeConverter<List<URI>, String> {

    /**
     * Converts a list of uris to a string.
     *
     * @param uriList List of uris.
     * @return String representing of uris.
     */
    @Override
    public String convertToDatabaseColumn(final List<URI> uriList) {

        return uriList == null || uriList.isEmpty() ? "" : String.join(",", uriList.toString());
    }

    /**
     * Converts a string to a list of uri.
     *
     * @param joinedList List of uris in string representation.
     * @return List of uri.
     */
    @Override
    public List<URI> convertToEntityAttribute(final String joinedList) {
        final var uriList = new ArrayList<URI>();
        if (joinedList == null) {
            return uriList;
        }
        final var stringList = joinedList.split(",");
        for (var s : stringList) {
            uriList.add(URI.create(s));
        }
        return uriList;
    }
}
