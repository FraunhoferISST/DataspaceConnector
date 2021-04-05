package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolicyPatternTest {

    @Test
    public void toString_nothing_correctMsg() {
        /* ARRANGE */
        final var input = PolicyPattern.PROVIDE_ACCESS;

        /* ACT */
        final var msg = input.toString();

        /* ASSERT */
        assertEquals("PROVIDE_ACCESS", msg);
    }

}
