package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import de.fraunhofer.isst.dataspaceconnector.filter.httptracing.internal.RequestWrapper;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
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
public class HttpTraceFilter extends OncePerRequestFilter {
    /**
     * The trace id.
     */
    private UUID traceId;

    /**
     * The event handler.
     */
    private final HttpTraceEventHandler eventHandler;

    /**
     * The constructor.
     *
     * @param eventHandler The handler responsible for HttpTrace events raised by this class.
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public HttpTraceFilter(final HttpTraceEventHandler eventHandler) {
        super();
        this.eventHandler = eventHandler;
    }

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
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.url = request.getRequestURI();
        trace.method = request.getMethod();
        trace.client = request.getRemoteAddr();

        trace.headers = "{";
        final var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final var key = headerNames.nextElement();
            final var value = request.getHeader(key);
            trace.headers += key + ": " + value;

            if (headerNames.hasMoreElements()) {
                trace.headers += ", ";
            }
        }
        trace.headers += "}";

        trace.parameterMap = "{";
        final var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final var key = parameterNames.nextElement();
            final var value = request.getHeader(key);
            trace.parameterMap += key + ": " + value;

            if (parameterNames.hasMoreElements()) {
                trace.parameterMap += ", ";
            }
        }
        trace.parameterMap += "}";

        try {
            trace.body = new String(request.getRequestBody(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        eventHandler.sendHttpTraceEvent(trace);
    }

    private void afterRequest(final ContentCachingResponseWrapper responseWrapper) {
        final var trace = new HttpTrace();
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.status = responseWrapper.getStatus();
        trace.body = getResponseAsPayload(responseWrapper);

        trace.headers = "{";
        final var headerNames = responseWrapper.getHeaderNames();
        for (final var key : headerNames) {
            final var value = responseWrapper.getHeader(key);
            trace.headers += key + ": " + value;

            if (key != headerNames.toArray()[headerNames.toArray().length - 1]) {
                trace.headers += ", ";
            }
        }
        trace.headers += "}";

        eventHandler.sendHttpTraceEvent(trace);
    }

    private String getResponseAsPayload(final ContentCachingResponseWrapper wrappedResponse) {
        String response = "";
        try {
            if (wrappedResponse.getContentSize() > 0) {
                response = new String(wrappedResponse.getContentAsByteArray(), 0,
                        wrappedResponse.getContentSize(),
                        wrappedResponse.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            response = "ERROR";
        }

        return response;
    }
}
