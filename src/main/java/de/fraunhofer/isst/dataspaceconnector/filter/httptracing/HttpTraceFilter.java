package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import de.fraunhofer.isst.dataspaceconnector.filter.httptracing.internal.RequestWrapper;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use this class to log all incoming and outgoing http traffic.
 */
@Component
@Order(1)
public final class HttpTraceFilter extends OncePerRequestFilter {
    /**
     * The class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTraceFilter.class);

    /**
     * The trace id.
     */
    private transient UUID traceId;

    /**
     * The event handler.
     */
    private final transient HttpTraceEventHandler eventHandler;

    /**
     * The constructor.
     *
     * @param traceEventHandler The handler responsible for HttpTrace events.
     */
    public HttpTraceFilter(final HttpTraceEventHandler traceEventHandler) {
        super();
        this.eventHandler = traceEventHandler;
    }

    /**
     * Generate some random uuid.
     *
     * @return The new uuid.
     */
    private static UUID generateUUID() {
        return UUIDUtils.createUUID(uuid -> false);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        final var requestWrapper = new RequestWrapper(request);
        final var responseWrapper = new ContentCachingResponseWrapper(response);

        traceId = generateUUID();
        beforeRequest(requestWrapper);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            afterRequest(responseWrapper);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void beforeRequest(final RequestWrapper request) {
        final var trace = new HttpTrace();
        trace.setId(traceId);
        trace.setTimestamp(LocalDateTime.now());
        trace.setUrl(request.getRequestURI());
        trace.setMethod(request.getMethod());
        trace.setClient(request.getRemoteAddr());

        var builder = new StringBuilder();
        builder.append("{");
        final var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final var key = headerNames.nextElement();
            final var value = request.getHeader(key);
            builder.append(key).append(": ").append(value);

            if (headerNames.hasMoreElements()) {
                builder.append(", ");
            }
        }
        builder.append("}");
        trace.setHeaders(builder.toString());

        builder = new StringBuilder();
        builder.append("{");
        final var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final var key = parameterNames.nextElement();
            final var value = request.getHeader(key);
            builder.append(key).append(": ").append(value);

            if (parameterNames.hasMoreElements()) {
                builder.append(", ");
            }
        }
        builder.append("}");
        trace.setParameterMap(builder.toString());

        try {
            trace.setBody(new String(request.getRequestBody(), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            LOGGER.warn("Failed to encode requestbody. [exception=({})]", exception.getMessage());
        } catch (NullPointerException ignored) {
        }

        eventHandler.sendHttpTraceEvent(trace);
    }

    private void afterRequest(final ContentCachingResponseWrapper responseWrapper) {
        final var trace = new HttpTrace();
        trace.setId(traceId);
        trace.setTimestamp(LocalDateTime.now());
        trace.setStatus(responseWrapper.getStatus());
        trace.setBody(getResponseAsPayload(responseWrapper));

        final var builder = new StringBuilder();
        builder.append("{");
        final var headerNames = responseWrapper.getHeaderNames();
        for (final var key : headerNames) {
            final var value = responseWrapper.getHeader(key);
            builder.append(key).append(": ").append(value);

            if (key != headerNames.toArray()[headerNames.toArray().length - 1]) {
                builder.append(", ");
            }
        }
        builder.append("}");
        trace.setHeaders(builder.toString());

        eventHandler.sendHttpTraceEvent(trace);
    }

    private String getResponseAsPayload(final ContentCachingResponseWrapper wrappedResponse) {
        String response = "";
        try {
            if (wrappedResponse.getContentSize() > 0) {
                response = new String(wrappedResponse.getContentAsByteArray(), 0,
                        wrappedResponse.getContentSize(), wrappedResponse.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException exception) {
            LOGGER.warn("Failed to encode http message. [exception=({})]", exception.getMessage());
        }

        return response;
    }
}
