package io.dataspaceconnector.model.utils;

import lombok.NoArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A converter for converting list of uris to a list of strings or to convert a list of strings to a list of uris.
 */
@Converter
@NoArgsConstructor
public class ListUriConverter implements AttributeConverter<List<URI>, List<String>> {

    /**
     * Converts a list of uris to a string.
     *
     * @param uriList List of uris.
     * @return List of string representing uris.
     */
    @Override
    public List<String> convertToDatabaseColumn(final List<URI> uriList) {
        final var list = new ArrayList<String>();
        for (var uri : uriList) {
            list.add(uri.toString());
        }
        return list;
    }

    /**
     * Converts a list of strings to a list of uri.
     *
     * @param joinedList List of uris in string representation.
     * @return List of uri.
     */
    @Override
    public List<URI> convertToEntityAttribute(final List<String> joinedList) {
        final ArrayList<URI> uriList = new ArrayList<>();
        for (var s : joinedList) {
            uriList.add(URI.create(s));
        }
        return uriList;
    }
}
