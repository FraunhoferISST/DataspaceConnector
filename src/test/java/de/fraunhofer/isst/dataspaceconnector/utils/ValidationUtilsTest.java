package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import org.junit.jupiter.api.Test;

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
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_headersNull_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(null);
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_headersEmpty_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(new ConcurrentHashMap<>());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_paramsNull_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(null);
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_paramsEmpty_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(new ConcurrentHashMap<>());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_pathVariablesNull_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(null);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_pathVariablesEmpty_passWithoutException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(new ConcurrentHashMap<>());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry(null, "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("", "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("   ", "value"));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", null));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", ""));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInHeaders_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getMapWithEntry("key", "   "));
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry(null, "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("", "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("   ", "value"));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", null));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", ""));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInParams_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getMapWithEntry("key", "   "));
        queryInput.setPathVariables(getValidMap());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry(null, "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("", "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankKeyInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("   ", "value"));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_nullValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", null));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_emptyValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", ""));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    @Test
    public void validateQueryInput_blankValueInPathVariables_throwIllegalArgumentException() {
        /* ARRANGE */
        QueryInput queryInput = new QueryInput();
        queryInput.setHeaders(getValidMap());
        queryInput.setParams(getValidMap());
        queryInput.setPathVariables(getMapWithEntry("key", "   "));

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class ,() -> ValidationUtils.validateQueryInput(queryInput));
    }

    private Map<String, String> getValidMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("validKey", "validValue");
        return map;
    }

    private Map<String, String> getMapWithEntry(String key, String value) {
        //use Hashmap here as is allows null keys and values
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
