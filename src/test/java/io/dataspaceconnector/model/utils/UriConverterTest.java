package io.dataspaceconnector.model.utils;

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
