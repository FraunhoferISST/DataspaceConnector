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
 * Use this class to wrap incoming HTTP requests too read the message payload multiple times.
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody = new byte[0];
    private boolean isBufferFilled = false;

    /**
     * The Constructor
     *
     * @param request The request to be wrapped
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Get the request body of the message
     *
     * @return The request body
     * @throws IOException if the request body could not be read
     */
    public byte[] getRequestBody() throws IOException {
        if (isBufferFilled) {
            return Arrays.copyOf(requestBody, requestBody.length);
        }

        var inputStream = super.getInputStream();
        if(inputStream != null){
            var buffer = new byte[128];
            var bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                requestBody = Bytes.concat(requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
            }

            isBufferFilled = true;
        }

        return requestBody;
    }

    /**
     * Get the request body of the message as stream
     *
     * @return The request body as stream
     * @throws IOException if the request body could not be read
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(getRequestBody());
    }

    private static class CustomServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream buffer;

        public CustomServletInputStream(byte[] contents) {
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
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
    }
}
