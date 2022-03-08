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
package io.dataspaceconnector.model.pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for inputs of a policy pattern that describes the restriction of the number of data usages.
 */
@Schema(example = "{\n"
        + "\t\"type\": \"N_TIMES_USAGE\",\n"
        + "\t\"value\": \"5\"\n"
        + "}")
@Data
@EqualsAndHashCode(callSuper = true)
public class UsageNumberDesc extends PatternDesc {

    /**
     * The number of usages.
     */
    @JsonProperty("value")
    private String value;
}
