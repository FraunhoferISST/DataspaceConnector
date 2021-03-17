package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessagesTest {

    @Test
    void toString_nothing_correctMsg() {
        /* ARRANGE */
        final var input = ErrorMessages.DESC_NULL;

        /* ACT */
        final var msg = input.toString();

        /* ASSERT */
        assertEquals("The description parameter may not be null.", msg);
    }
}
