package io.dataspaceconnector.model.utils;

import lombok.NoArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter for converting list of uris to a string or to convert a string to a list of uris.
 */
@Converter
@NoArgsConstructor
public class ListUriConverter implements AttributeConverter<List<URI>, String> {

    /**
     * Converts a list of uri to a string.
     *
     * @param uriList List of uris.
     * @return String representation of the list.
     */
    @Override
    public String convertToDatabaseColumn(List<URI> uriList) {
        return String.join(",", uriList.toString());
    }

    /**
     * Converts the string representation to a list of uri.
     *
     * @param joinedList String representation of the list.
     * @return List of uri.
     */
    @Override
    public List<URI> convertToEntityAttribute(String joinedList) {
        final var strings = joinedList.split(",");
        final ArrayList<URI> uriList = new ArrayList<>();
        for (var s : strings) {
            uriList.add(URI.create(s));
        }
        return uriList;
    }
}
