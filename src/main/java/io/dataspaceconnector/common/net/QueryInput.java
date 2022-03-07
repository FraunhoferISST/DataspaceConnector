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
package io.dataspaceconnector.common.net;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query for a backend.
 */
@Schema(
        name = "QueryInput",
        description = "Query parameters, headers and path variables as maps",
        implementation = QueryInput.class,
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
    private Map<String, String> headers = new HashMap<>();

    /**
     * Params to use for querying a backend.
     */
    private Map<String, String> params = new HashMap<>();

    /**
     * Path variables to use for querying a backend.
     */
    private Map<String, String> pathVariables = new HashMap<>();

    /**
     * Path extending the base path of an api request.
     */
    private String optional;

}
