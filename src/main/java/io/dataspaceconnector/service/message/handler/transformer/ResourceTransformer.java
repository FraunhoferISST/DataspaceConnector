package io.dataspaceconnector.service.message.handler.transformer;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.exception.DeserializationException;
import io.dataspaceconnector.service.message.handler.exception.MissingPayloadException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Transforms the payload of a ResourceUpdateMessage from a {@link MessagePayload} to a
 * {@link Resource}.
 */
@Component("ResourceDeserializer")
@RequiredArgsConstructor
class ResourceTransformer extends IdsTransformer<
        Request<ResourceUpdateMessageImpl, MessagePayload, Optional<Jws<Claims>>>,
        RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull
    DeserializationService deserializationService;

    /**
     * Deserializes the payload of a ResourceUpdateMessage to a Resource.
     *
     * @param msg the incoming message.
     * @return a RouteMsg object with the initial header and the Resource as payload.
     * @throws Exception if the payload cannot be read or deserialized.
     */
    @Override
    protected RouteMsg<ResourceUpdateMessageImpl, Resource> processInternal(
            final Request<ResourceUpdateMessageImpl, MessagePayload, Optional<Jws<Claims>>> msg)
            throws Exception {

        final String payloadString;
        try {
            payloadString = MessageUtils.getStreamAsString(msg.getBody());
        } catch (IllegalArgumentException e) {
            throw new MissingPayloadException("Payload is missing from ResourceUpdateMessage.", e);
        }

        if (payloadString.isBlank()) {
            throw new MissingPayloadException("Payload is missing from ResourceUpdateMessage.");
        }

        final Resource resource;
        try {
            resource = deserializationService.getResource(payloadString);
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("Deserialization failed.", e);
        }

        return new Request<>(msg.getHeader(), resource, msg.getClaims());
    }

}
