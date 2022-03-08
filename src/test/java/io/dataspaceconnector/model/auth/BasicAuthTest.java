/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.common.net.HttpService;
import nl.jqno.equalsverifier.EqualsVerifier;
import okhttp3.Credentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthTest {

    private final String username = "user";
    private final String password = "pwd";
    private final String credentials = Credentials.basic(username, password);
    private final HttpService.Pair authHeader = new HttpService.Pair("Authorization", credentials);
    private final BasicAuth            basicAuth = new BasicAuth(username, password);
    private final HttpService.HttpArgs args      = new HttpService.HttpArgs();

    @Test
    void getAuthPair_validContent_getAuthorization() {
        /* ARRANGE */

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals("Authorization", args.getAuth().getFirst());
    }

    @Test
    void getAuthPair_validContent_getValue2() {
        /* ARRANGE */

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals(credentials, args.getAuth().getSecond());
    }

    @Test
    void constructor_validDesc_willAssign() {
        /* ARRANGE */
        final var desc = new AuthenticationDesc(username, password);

        /* ACT */
        final var basicAuth = new BasicAuth(desc);

        /* ASSERT */
        assertEquals(username, basicAuth.getUsername());
        assertEquals(password, basicAuth.getPassword());
    }

    @Test
    void setAuth_validValue_willAddAuthHeader() {
        /* ARRANGE */

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals(authHeader, args.getAuth());
    }

    @Test
    void setAuth_validValueButAuthAlreadySet_willNotUpdate() {
        /* ARRANGE */
        args.setAuth(new HttpService.Pair("Authorization", Credentials.basic("test", "test")));

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertNotEquals(authHeader, args.getAuth());
    }

    @Test
    void setAuth_validValueArgsHasSomeKeySet_willNotUpdate() {
        /* ARRANGE */
        args.setAuth(new HttpService.Pair("Authorization", null));

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertNotEquals(authHeader, args.getAuth());
    }

    @Test
    void setAuth_validValueArgsHasSomeValueSet_willNotUpdate() {
        /* ARRANGE */
        args.setAuth(new HttpService.Pair(null, credentials));

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertNotEquals(authHeader, args.getAuth());
    }

    @Test
    void setAuth_validValueArgsHasNoValuesSet_willUpdate() {
        /* ARRANGE */
        args.setAuth(new HttpService.Pair(null, null));

        /* ACT */
        basicAuth.setAuth(args);

        /* ASSERT */
        assertEquals(authHeader, args.getAuth());
    }

    @Test
    void equalsAndHash_willPass() {
        EqualsVerifier.simple().forClass(BasicAuth.class)
        .withIgnoredFields("id", "deleted")
        .verify();
    }

    @Test
    void basicauth_setter_willOverwrite() {
        /* ARRANGE */
        final var auth = new BasicAuth("", "");

        /* ACT */
        auth.setUsername(username);
        auth.setPassword(password);

        /* ASSERT */
        assertEquals(username, auth.getUsername());
        assertEquals(password, auth.getPassword());
    }
}
