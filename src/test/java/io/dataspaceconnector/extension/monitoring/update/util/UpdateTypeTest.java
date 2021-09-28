package io.dataspaceconnector.extension.monitoring.update.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateTypeTest {

    @Test
    public void toString_nothing_correctMsg() {
        /* ARRANGE */
        final var input = UpdateType.NO_UPDATE;

        /* ACT */
        final var msg = input.toString();

        /* ASSERT */
        assertEquals("None", msg);
    }
}
