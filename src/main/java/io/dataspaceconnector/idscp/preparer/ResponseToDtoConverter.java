package io.dataspaceconnector.idscp.preparer;

import java.nio.charset.StandardCharsets;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

/**
 * Converts a response received over IDSCPv2 to a response DTO.
 */
@Component("ResponseToDtoConverter")
public class ResponseToDtoConverter extends Idscp2MappingProcessor {

    /**
     * Converts an incoming response message to q {@link Response}.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var header = in
                .getHeader(ParameterUtils.IDSCP_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payload = new String(in.getBody(byte[].class), StandardCharsets.UTF_8);

        in.setBody(new Response(header, payload));
    }

}
