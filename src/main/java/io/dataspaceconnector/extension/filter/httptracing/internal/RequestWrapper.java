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
package io.dataspaceconnector.extension.filter.httptracing.internal;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Wraps incoming HTTP requests too read the message payload multiple times.
 */
@Log4j2
public final class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * The original request body.
     */
    private transient byte[] requestBody;

    /**
     * Whether the original request body has been copied.
     */
    private transient boolean isBufferFilled;

    /**
     * Default constructor.
     *
     * @param request The request to be wrapped
     */
    public RequestWrapper(final HttpServletRequest request) {
        super(request);
    }

    /**
     * Get the request body of the message.
     *
     * @return The request body.
     * @throws IOException if the request body could not be read.
     */
    public byte[] getRequestBody() throws IOException {
        final var output = isBufferFilled ? requestBody : cloneRequestBody();
        return Arrays.copyOf(output, output.length);
    }

    private byte[] cloneRequestBody() throws IOException {
        final var inputStream = super.getInputStream();
        if (inputStream != null) {
            requestBody = StreamUtils.copyToByteArray(inputStream);
            isBufferFilled = true;
        }

        return requestBody;
    }

    /**
     * Get the request body of the message as stream.
     *
     * @return The request body as stream.
     * @throws IOException if the request body could not be read.
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(getRequestBody());
    }

    @Override
    public BufferedReader getReader() throws UnsupportedEncodingException {
        final var bais = new ByteArrayInputStream(this.requestBody);
        return new BufferedReader(new InputStreamReader(bais, this.getCharacterEncoding()));
    }

    /**
     * Custom input stream returning a clone of the original request.
     */
    private static class CustomServletInputStream extends ServletInputStream {
        /**
         * Copy of the input stream.
         */
        private final transient ByteArrayInputStream buffer;

        /**
         * Default constructor.
         *
         * @param contents The request body.
         */
        /* default */ CustomServletInputStream(final byte[] contents) {
            super();
            buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(final ReadListener listener) {
            if (log.isErrorEnabled()) {
                log.error("Tried to set read listener.");
            }
        }
    }
}
