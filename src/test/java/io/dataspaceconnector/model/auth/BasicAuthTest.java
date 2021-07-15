package io.dataspaceconnector.model.auth;

import okhttp3.Credentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthTest {

    @Test
    void getAuthPair_nullValue_nullValue() {
        /* ARRANGE */
        var basicAuth = new BasicAuth();

        /* ACT */
        var result = basicAuth.getAuthPair();

        /* ASSERT */
        assertNull(result);
    }

    @Test
    void getAuthPair_validContent_getAuthorization() {
        /* ARRANGE */
        var username = "user";
        var password = "pw";
        var basicAuth = new BasicAuth(username, password);

        /* ACT */
        var result = basicAuth.getAuthPair();

        /* ASSERT */
        assertEquals("Authorization", result.component1());
    }

    @Test
    void getAuthPair_validContent_getValue2() {
        /* ARRANGE */
        var username = "user";
        var password = "pw";
        var basicAuth = new BasicAuth(username, password);

        var expected = Credentials.basic(username, password);

        /* ACT */
        var result = basicAuth.getAuthPair();

        /* ASSERT */
        assertEquals(expected, result.component2());
    }
}
