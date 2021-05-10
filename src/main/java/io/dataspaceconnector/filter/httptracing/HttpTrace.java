package io.dataspaceconnector.filter.httptracing;

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
