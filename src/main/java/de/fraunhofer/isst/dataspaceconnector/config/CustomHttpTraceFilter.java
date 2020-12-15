package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.dataspaceconnector.controller.BrokerController;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

@Component
public class CustomHttpTraceFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

    private UUID traceId;
    private HttpTraceEventHandler eventHandler;

    public CustomHttpTraceFilter(HttpTraceEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  throws ServletException, IOException {
        final var requestWrapper = new ContentCachingRequestWrapper(request);
        final var responseWrapper = new ContentCachingResponseWrapper(response);

        traceId = UUIDUtils.generateUUID();
        beforeRequest(requestWrapper);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            afterRequest(requestWrapper, responseWrapper);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void beforeRequest(ContentCachingRequestWrapper request) {
        final var trace = new HttpTrace();
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.uri = request.getRequestURI();
        trace.method = request.getMethod();
        trace.parameterMap = request.getParameterMap();
        trace.body = getRequestAsPayload(request);

        eventHandler.sendHttpTraceEvent(trace);
    }

    private void afterRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) throws IOException {
        final var trace = new HttpTrace();
        trace.id = traceId;
        trace.timestamp = LocalDateTime.now();
        trace.status = responseWrapper.getStatus();
        trace.body = getResponseAsPayload(responseWrapper);

        eventHandler.sendHttpTraceEvent(trace);
    }

    private String getRequestAsPayload(HttpServletRequest request) {
        final var wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if(wrapper != null)
            return "";

        final var buffer = wrapper.getContentAsByteArray();
        if(buffer.length > 0){
            try {
                return new String(buffer, 0, buffer.length, wrapper.getCharacterEncoding());
            }catch(UnsupportedEncodingException exception) {
                return "ERROR";
            }
        }

        return "";
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
