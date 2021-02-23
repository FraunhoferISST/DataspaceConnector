package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class stores information about a http connection.
 */
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class HttpTrace {

    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private LocalDateTime timestamp;

    private String method;
    private String url;
    private String body;
    private String headers;
    private int status;
    private String client;
    private String parameterMap;
}
