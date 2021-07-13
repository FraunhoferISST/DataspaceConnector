package io.dataspaceconnector.service.message.type.exceptions;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidResponseTest {

    @Test
    public void new_content_hasContent() {
        /* ARRANGE */
        final var content = new HashMap<String, Object>();
        content.put("Key", "Value");

        final var exception = new InvalidResponse(content);

        /* ACT */
        final var result = exception.getContent();

        /* ASSERT */
        assertEquals(content, result);
    }

    @Test
    public void new_contentAndException_hasContentAndException() {
        /* ARRANGE */
        final var content = new HashMap<String, Object>();
        content.put("Key", "Value");

        final var throwable = new RuntimeException("HELLO");

        final var exception = new InvalidResponse(content, throwable);

        /* ACT */
        final var result = exception.getContent();
        final var cause = exception.getCause();

        /* ASSERT */
        assertEquals(content, result);
        assertEquals(throwable, cause);
    }
}
