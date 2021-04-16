package de.fraunhofer.isst.dataspaceconnector.filter.httptracing.internal;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Use this class to wrap incoming HTTP requests too read the message payload multiple times.
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;
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
            return requestBody.clone();
        }

        var inputStream = super.getInputStream();
        if(inputStream != null){
            requestBody = StreamUtils.copyToByteArray(inputStream);
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
