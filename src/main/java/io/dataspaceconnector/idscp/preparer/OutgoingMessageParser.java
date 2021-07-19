package io.dataspaceconnector.idscp.preparer;

import java.nio.charset.StandardCharsets;

import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

/**
 * Converts a response DTO to an IDSCPv2 message.
 */
@Component("OutgoingIdscpMessageParser")
public class OutgoingMessageParser extends Idscp2MappingProcessor {

    /**
     * Creates an IDSCPv2 message with header and payload from a {@link Response}.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var response = in.getBody(Response.class);

        if (response != null) {
            in.setHeader(ParameterUtils.IDSCP_HEADER, response.getHeader());
            in.setBody(response.getBody().getBytes(StandardCharsets.UTF_8));
        } else {
            final var rejection = in.getBody(ErrorResponse.class);
            in.setHeader(ParameterUtils.IDSCP_HEADER, rejection.getRejectionMessage());
            in.setBody(rejection.getErrorMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

}
