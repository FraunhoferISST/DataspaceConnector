package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import de.fraunhofer.isst.dataspaceconnector.filter.httptracing.internal.RequestWrapper;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
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

    private UUID traceId;
    private final HttpTraceEventHandler eventHandler;

    /**
     * Constructor
     *
     * @param eventHandler The handler responsible for HttpTrace events raised by this class
     */
    public HttpTraceFilter(HttpTraceEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    private static UUID generateUUID() {
        return UUIDUtils.createUUID(uuid -> false);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
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

    private void beforeRequest(RequestWrapper request) {
        final var trace = new HttpTrace();
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.url = request.getRequestURI();
        trace.method = request.getMethod();
        trace.client = request.getRemoteAddr();

        trace.headers = "{";
        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var key = headerNames.nextElement();
            var value = request.getHeader(key);
            trace.headers += key + ": " + value;

            if (headerNames.hasMoreElements()) {
                trace.headers += ", ";
            }
        }
        trace.headers += "}";

        trace.parameterMap = "{";
        var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            var key = parameterNames.nextElement();
            var value = request.getHeader(key);
            trace.parameterMap += key + ": " + value;

            if (parameterNames.hasMoreElements()) {
                trace.parameterMap += ", ";
            }
        }
        trace.parameterMap += "}";

        try{
            trace.body = new String(request.getRequestBody(), StandardCharsets.UTF_8);
        }catch(IOException exception){
            exception.printStackTrace();
        }

        eventHandler.sendHttpTraceEvent(trace);
    }

    private void afterRequest(
        ContentCachingResponseWrapper responseWrapper) {
        final var trace = new HttpTrace();
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.status = responseWrapper.getStatus();
        trace.body = getResponseAsPayload(responseWrapper);

        trace.headers = "{";
        var headerNames = responseWrapper.getHeaderNames();
        for (var key : headerNames) {
            var value = responseWrapper.getHeader(key);
            trace.headers += key + ": " + value;

            if (key != headerNames.toArray()[headerNames.toArray().length - 1]) {
                trace.headers += ", ";
            }
        }
        trace.headers += "}";

        eventHandler.sendHttpTraceEvent(trace);
    }

    private String getResponseAsPayload(ContentCachingResponseWrapper wrappedResponse) {
        try {
            if (wrappedResponse.getContentSize() <= 0) {
                return "";
            }

            return new String(wrappedResponse.getContentAsByteArray(), 0,
                wrappedResponse.getContentSize(),
                wrappedResponse.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "ERROR";
        }
    }
}
