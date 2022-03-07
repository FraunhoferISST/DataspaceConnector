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

import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.extension.filter.httptracing.internal.RequestWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * Use this class to log all incoming and outgoing http traffic.
 */
@Component
@Order(1)
@Log4j2
@ConditionalOnProperty(name = "httptrace.enabled")
public final class HttpTraceFilter extends OncePerRequestFilter {
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
     * @param handler Responsible for HttpTrace events raised by this class.
     */
    public HttpTraceFilter(final HttpTraceEventHandler handler) {
        super();
        this.eventHandler = handler;
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
        trace.setTraceId(traceId);
        trace.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
        trace.setUrl(request.getRequestURI());
        trace.setMethod(request.getMethod());
        trace.setClient(request.getRemoteAddr());

        trace.setHeaders(new HashMap<>());
        final var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final var key = headerNames.nextElement();
            trace.getHeaders().put(key, request.getHeader(key));
        }

        trace.setParameterMap(new HashMap<>());
        final var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final var key = parameterNames.nextElement();
            trace.getParameterMap().put(key, request.getHeader(key));
        }

        try {
            trace.setBody(new String(request.getRequestBody(), request.getCharacterEncoding()));
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get the request body. [exception=({})]", e.getMessage(), e);
            }
        }

        eventHandler.sendHttpTraceEvent(trace);
    }

    private void afterRequest(final ContentCachingResponseWrapper responseWrapper) {
        final var trace = new HttpTrace();
        trace.setTraceId(traceId);
        trace.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
        trace.setStatus(responseWrapper.getStatus());
        trace.setBody(getResponseAsPayload(responseWrapper));

        trace.setHeaders(new HashMap<>());
        for (final var key : responseWrapper.getHeaderNames()) {
            trace.getHeaders().put(key, responseWrapper.getHeader(key));
        }

        eventHandler.sendHttpTraceEvent(trace);
    }

    private String getResponseAsPayload(final ContentCachingResponseWrapper wrappedResponse) {
        var response = "ERROR";
        if (wrappedResponse.getContentSize() > 0) {
            try {
                response = new String(wrappedResponse.getContentAsByteArray(), 0,
                        wrappedResponse.getContentSize(),
                        wrappedResponse.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to get the response. [exception=({})]", e.getMessage(), e);
                }
            }
        }

        return response;
    }
}
