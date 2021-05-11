package io.dataspaceconnector.model.messages;

import java.net.URI;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DescriptionRequestMessageDescTest {
    @Test
    void defaultConstructor_nothing_emptyDesc() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new DescriptionRequestMessageDesc();

        /* ASSERT */
        assertNull(result.getRequestedElement());
        assertNull(result.getRecipient());
    }

    @Test
    void allArgsConstructor_valid_validDesc() {
        /* ARRANGE */
        final var recipient = URI.create("someRecipient");
        final var element = URI.create("someElement");

        /* ACT */
        final var result = new DescriptionRequestMessageDesc(recipient, element);

        /* ASSERT */
        assertEquals(recipient, result.getRecipient());
        assertEquals(element, result.getRequestedElement());
    }

    @Test
    void equals_everything_valid() {
        EqualsVerifier.simple().forClass(DescriptionRequestMessageDesc.class).verify();
    }
}
