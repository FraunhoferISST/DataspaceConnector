package io.dataspaceconnector.view.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ViewConstantsTest {
    @Test
    public void constructor_is_hidden() {
        assertThrows(UnsupportedOperationException.class, ViewConstants::new);
    }
}
