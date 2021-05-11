package io.dataspaceconnector.model.messages;

import java.net.URI;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageDescTest {
    @Test
    void defaultConstructor_nothing_emptyDesc() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new MessageDesc();

        /* ASSERT */
        assertNull(result.getRecipient());
    }

    @Test
    void allArgsConstructor_valid_validDesc() {
        /* ARRANGE */
        final var recipient = URI.create("someRecipient");

        /* ACT */
        final var result = new MessageDesc(recipient);

        /* ASSERT */
        assertEquals(recipient, result.getRecipient());
    }

    @Test
    void equals_everything_valid() {
        EqualsVerifier.simple().forClass(MessageDesc.class).verify();
    }
}
