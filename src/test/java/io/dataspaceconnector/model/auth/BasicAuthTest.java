/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
