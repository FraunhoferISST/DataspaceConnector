package de.fraunhofer.isst.dataspaceconnector.filter.httptracing.internal;

import com.google.common.primitives.Bytes;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Used to wrap incoming HTTP requests for reading payload multiple times.
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * The copy of the request body.
     */
    private byte[] requestBody;

    /**
     * If the requestBody is filled.
     */
    private boolean isBufferFilled;

    /**
     * The Constructor.
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
        byte[] output;

        if (isBufferFilled) {
            output = Arrays.copyOf(requestBody, requestBody.length);
        } else {

            final var inputStream = super.getInputStream();
            if (inputStream != null) {
                final var buffer = new byte[128];
                var bytesRead = inputStream.read(buffer);
                while (bytesRead != -1) {
                    requestBody = Bytes.concat(requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
                    bytesRead = inputStream.read(buffer);
                }

                isBufferFilled = true;
            }

            output = requestBody;
        }

        return output;
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

    /**
     * Custom overload for returning the request body.
     */
    private static class CustomServletInputStream extends ServletInputStream {
        /**
         * The stream this servlet operates on.
         */
        private final ByteArrayInputStream buffer;

        /**
         * The constructor.
         * @param contents The content this stream operates on.
         */
        public CustomServletInputStream(final byte[] contents) {
            super();

            this.buffer = new ByteArrayInputStream(contents);
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
            throw new RuntimeException("Not implemented");
        }
    }
}
