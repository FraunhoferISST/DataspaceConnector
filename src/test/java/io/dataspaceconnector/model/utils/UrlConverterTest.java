package io.dataspaceconnector.model.utils;

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
