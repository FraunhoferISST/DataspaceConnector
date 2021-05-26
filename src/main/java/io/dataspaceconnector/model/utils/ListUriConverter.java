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
