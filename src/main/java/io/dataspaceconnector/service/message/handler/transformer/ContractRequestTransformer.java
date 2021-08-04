package io.dataspaceconnector.service.message.handler.transformer;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Transforms the payload of a ContractRequestMessage from a {@link MessagePayload} to a
 * {@link ContractRequest}.
 */
@Component("ContractDeserializer")
@RequiredArgsConstructor
class ContractRequestTransformer extends IdsTransformer<
        Request<ContractRequestMessageImpl, MessagePayload, Optional<Jws<Claims>>>,
        RouteMsg<ContractRequestMessageImpl, ContractRequest>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull
    DeserializationService deserializationService;

    /**
     * Deserializes the payload of a ContractRequestMessage to a ContractRequest.
     *
     * @param msg the incoming message.
     * @return a RouteMsg object with the initial header and the ContractRequest as payload.
     * @throws Exception if the payload cannot be deserialized.
     */
    @Override
    protected RouteMsg<ContractRequestMessageImpl, ContractRequest> processInternal(
            final Request<ContractRequestMessageImpl, MessagePayload, Optional<Jws<Claims>>> msg)
            throws Exception {
        final var contract = deserializationService
                .getContractRequest(MessageUtils.getPayloadAsString(msg.getBody()));
        return new Request<>(msg.getHeader(), contract, msg.getClaims());
    }

}
