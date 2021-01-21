package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class stores information about a http connection
 */
@Data
@JsonInclude(Include.NON_NULL)
public class HttpTrace {

    public UUID id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    public LocalDateTime timestamp;
    public String method;
    public String url;
    public String body;
    public String headers;
    public int status;
    public String client;
    public String parameterMap;
}
