package de.fraunhofer.isst.dataspaceconnector.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;

@Schema(
        name = "QueryInput",
        description = "Query parameters as map",
        oneOf = QueryInput.class,
        example = "{\n" +
                "  \"headers\": {\n" +
                "    \"key\": \"value\"\n" +
                "  },\n" +
                "  \"params\": {\n" +
                "    \"key\": \"value\"\n" +
                "  }\n" +
                "}"
)
@Data
public class QueryInput {

    HashMap<String, String> headers = new HashMap<>();
    HashMap<String, String> params = new HashMap<>();
}
