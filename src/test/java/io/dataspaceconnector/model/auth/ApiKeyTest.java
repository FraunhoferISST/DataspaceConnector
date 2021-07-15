package io.dataspaceconnector.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

    @Test
    void getAuthPair_nullValue_nullComponent1() {
        /* ARRANGE */
        var apiKey = new ApiKey();

        /* ACT */
        var result = apiKey.getAuthPair();

        /* ASSERT */
        assertNull(result.component1());
    }

    @Test
    void getAuthPair_nullValue_nullComponent2() {
        /* ARRANGE */
        var apiKey = new ApiKey();

        /* ACT */
        var result = apiKey.getAuthPair();

        /* ASSERT */
        assertNull(result.component2());
    }

    @Test
    void getAuthPair_validContent_getValue1() {
        /* ARRANGE */
        var key = "key";
        var value = "value";
        var apiKey = new ApiKey(key, value);

        /* ACT */
        var result = apiKey.getAuthPair();

        /* ASSERT */
        assertEquals(key, result.component1());
    }

    @Test
    void getAuthPair_validContent_getValue2() {
        /* ARRANGE */
        var key = "key";
        var value = "value";
        var apiKey = new ApiKey(key, value);

        /* ACT */
        var result = apiKey.getAuthPair();

        /* ASSERT */
        assertEquals(value, result.component2());
    }
}
