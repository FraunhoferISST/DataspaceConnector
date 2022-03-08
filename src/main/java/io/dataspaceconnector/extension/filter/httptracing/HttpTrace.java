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
package io.dataspaceconnector.extension.filter.httptracing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * This class stores information about a http connection.
 */
@Data
@JsonInclude(Include.NON_NULL)
public class HttpTrace {

    /**
     * Trace id.
     */
    private UUID traceId;

    /**
     * Time of trace creation.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss.SSSZ")
    private ZonedDateTime timestamp;

    /**
     * Type of http call, eg. POST, GET, etc. Empty for responses.
     */
    private String method;

    /**
     * The message target url.
     */
    private String url;

    /**
     * The message body.
     */
    private String body;

    /**
     * The message header.
     */
    private Map<String, String> headers;

    /**
     * The message status.
     */
    private int status;

    /**
     * The message client.
     */
    private String client;

    /**
     * The message parameter map.
     */
    private Map<String, String> parameterMap;
}
