package io.dataspaceconnector.idscp.preparer;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsValidator;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

/**
 * Converts an incoming IDSCPv2 message to a request DTO.
 */
@Component("IncomingIdscpMessageParser")
@RequiredArgsConstructor
public class IncomingMessageParser extends Idscp2MappingProcessor {

    /**
     * Service for validating DATs.
     */
    private final @NonNull DapsValidator dapsValidator;

    /**
     * Creates a {@link Request} with the header and payload from the IDSCPv2 message. Also gets the
     * claims from the DAT and adds them to the request.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var header = in
                .getHeader(ParameterUtils.IDSCP_HEADER, de.fraunhofer.iais.eis.Message.class);
        final var payloadStream = new ByteArrayInputStream(in.getBody(byte[].class));
        final var payload = new MessagePayloadInputstream(payloadStream, new ObjectMapper());

        Optional<Jws<Claims>> claims;
        try {
            claims = Optional.of(dapsValidator.getClaims(header.getSecurityToken()));
        } catch (ClaimsException | ExpiredJwtException exception) {
            claims = Optional.empty();
        }

        in.setBody(new Request<>(header, payload, claims));
    }

}
