package io.dataspaceconnector.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTypeTest {

    @Test
    void getAuthPair_nullValue_nullComponent1() {
        /* ARRANGE */
        var authType = new AuthType();

        /* ACT */
        var result = authType.getAuthPair();

        /* ASSERT */
        assertNull(result.component1());
    }

    @Test
    void getAuthPair_nullValue_nullComponent2() {
        /* ARRANGE */
        var authType = new AuthType();

        /* ACT */
        var result = authType.getAuthPair();

        /* ASSERT */
        assertNull(result.component2());
    }

}
