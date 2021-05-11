package io.dataspaceconnector.model.messages;

import java.net.URI;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMessageDescTest {
    @Test
    void defaultConstructor_nothing_emptyDesc() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new NotificationMessageDesc();

        /* ASSERT */
        assertNull(result.getRecipient());
    }

    @Test
    void allArgsConstructor_valid_validDesc() {
        /* ARRANGE */
        final var recipient = URI.create("someRecipient");

        /* ACT */
        final var result = new NotificationMessageDesc(recipient);

        /* ASSERT */
        assertEquals(recipient, result.getRecipient());
    }

    @Test
    void equals_everything_valid() {
        EqualsVerifier.simple().forClass(NotificationMessageDesc.class).verify();
    }
}
