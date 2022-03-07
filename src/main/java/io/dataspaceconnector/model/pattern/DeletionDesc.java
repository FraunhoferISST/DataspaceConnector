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
 * Class for inputs of a policy pattern that describes the data deletion at a specific datetime.
 */
@Schema(example = "{\n"
        + "\t\"type\": \"USAGE_UNTIL_DELETION\",\n"
        + "\t\"start\": \"2020-07-11T00:00:00Z\",\n"
        + "\t\"end\": \"2020-07-11T00:00:00Z\",\n"
        + "\t\"date\": \"2020-07-11T00:00:00Z\"\n"
        + "}")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeletionDesc extends PatternDesc {

    /**
     * The start value of the usage interval.
     */
    @JsonProperty("start")
    private String start;

    /**
     * The end value of the usage interval.
     */
    @JsonProperty("end")
    private String end;

    /**
     * The datetime of data deletion.
     */
    @JsonProperty("date")
    private String date;
}
