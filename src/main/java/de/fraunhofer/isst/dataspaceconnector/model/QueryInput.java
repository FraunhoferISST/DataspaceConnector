package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query for a backend.
 */
@Schema(
        name = "QueryInput",
        description = "Query parameters, headers and path variables as maps",
        oneOf = QueryInput.class,
        example = "{\n"
                  + "  \"headers\": {\n"
                  + "    \"key\": \"value\"\n"
                  + "  },\n"
                  + "  \"params\": {\n"
                  + "    \"key\": \"value\"\n"
                  + "  },\n"
                  + "  \"pathVariables\": {\n"
                  + "    \"key\": \"value\"\n"
                  + "  }\n"
                  + "}"
)
@Data
public class QueryInput {

    /**
     * Headers to use for querying a backend.
     */
    private Map<String, String> headers = new ConcurrentHashMap<>();

    /**
     * Params to use for querying a backend.
     */
    private Map<String, String> params = new ConcurrentHashMap<>();

    /**
     * Path variables to use for querying a backend.
     */
    private Map<String, String> pathVariables = new ConcurrentHashMap<>();

    /**
     * Path extending the base path of an api request.
     */
    private String optional;

}
