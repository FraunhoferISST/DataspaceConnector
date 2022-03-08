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
package io.dataspaceconnector.common.util;

import io.dataspaceconnector.common.net.QueryInput;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationUtilsTest {

    @Test
    public void validateQueryInput_inputNull_passWithoutException() {
        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(null));
    }

    @Test
    public void validateQueryInput_validQueryInput_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_headersNull_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(null);
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_headersEmpty_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(new ConcurrentHashMap<>());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_paramsNull_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(null);
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_paramsEmpty_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(new ConcurrentHashMap<>());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_pathVariablesNull_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(null);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_pathVariablesEmpty_passWithoutException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(new ConcurrentHashMap<>());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry(null, "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("", "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("   ", "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", null));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", ""));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", "   "));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry(null, "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("", "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("   ", "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", null));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", ""));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", "   "));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry(null, "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("", "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("   ", "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", null));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", ""));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", "   "));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    private Map<String, String> getValidMap() {
        final var map = new ConcurrentHashMap<String, String>();
        map.put("validKey", "validValue");
        return map;
    }

    private Map<String, String> getMapWithEntry(String key, String value) {
        // Use Hashmap here as is allows null keys and values.
        final var map = new HashMap<String, String>();
        map.put(key, value);
        return map;
    }
}
