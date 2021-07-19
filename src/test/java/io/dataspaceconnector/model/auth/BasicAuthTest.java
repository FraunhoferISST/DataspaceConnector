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

import io.dataspaceconnector.service.HttpService;
import okhttp3.Credentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthTest {

    @Test
    void getAuthPair_nullValue_nullValue() {
        /* ARRANGE */

        /* ACT */
        var basicAuth = new BasicAuth();

        /* ASSERT */
        assertNull(basicAuth.getUsername());
        assertNull(basicAuth.getPassword());
    }

    @Test
    void getAuthPair_validContent_getAuthorization() {
        /* ARRANGE */
        var username = "user";
        var password = "pw";
        var basicAuth = new BasicAuth(username, password);

        final var args = new HttpService.HttpArgs();

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals("Authorization", args.getAuth().getFirst());
    }

    @Test
    void getAuthPair_validContent_getValue2() {
        /* ARRANGE */
        var username = "user";
        var password = "pw";
        var basicAuth = new BasicAuth(username, password);

        var expected = Credentials.basic(username, password);
        final var args = new HttpService.HttpArgs();

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals(expected, args.getAuth().getSecond());
    }
}
