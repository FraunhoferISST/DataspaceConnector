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
