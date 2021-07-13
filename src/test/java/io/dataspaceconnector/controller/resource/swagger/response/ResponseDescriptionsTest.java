package io.dataspaceconnector.controller.resource.swagger.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDescriptionsTest {
    @Test
    public void constructor_is_hidden() {
        assertThrows(UnsupportedOperationException.class, ResponseDescriptions::new);
    }
}
