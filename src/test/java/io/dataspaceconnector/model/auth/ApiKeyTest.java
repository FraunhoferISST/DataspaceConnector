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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

    @Test
    void getAuthPair_validContent_setHeader() {
        /* ARRANGE */
        var key = "key";
        var value = "value";
        var apiKey = new ApiKey(key, value);
        final var args = new HttpService.HttpArgs();

        /* ACT */
        apiKey.setAuth(args);

        /* ASSERT */
        assertTrue(args.getHeaders().containsKey(key));
        assertEquals(value, args.getHeaders().get(key));
    }
    @Test
    void getAuthPair_validContent_setterWillOverwrite_setHeader() {
        /* ARRANGE */
        var key = "key";
        var value = "value";
        var apiKey = new ApiKey("1", "2");

        /* ACT */
        apiKey.setKey(key);
        apiKey.setValue(value);

        /* ASSERT */
        assertEquals(key, apiKey.getKey());
        assertEquals(value, apiKey.getValue());
    }
}
