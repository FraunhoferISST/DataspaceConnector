package io.dataspaceconnector.idscp.preparer;

import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

/**
 * Prepares a request message for IDSCPv2 communication without a payload.
 */
@Component("RequestWithoutPayloadPreparer")
public class RequestWithoutPayloadPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with empty body for communication over IDSCPv2.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(null);
    }

}
