package de.fraunhofer.isst.dataspaceconnector.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsageControlFrameworkTest {

    @Test
    public void toString_nothing_returnValidOutput() {
        /* ARRANGE */
        final var input = UsageControlFramework.INTERNAL;

        /* ACT */
        final var inputAsString = input.toString();

        /* ASSERT */
        assertEquals("INTERNAL", inputAsString);
    }
}
