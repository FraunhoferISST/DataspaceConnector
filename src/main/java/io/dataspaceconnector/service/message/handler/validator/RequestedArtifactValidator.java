package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.NoRequestedArtifactException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates the requested artifact given in an ArtifactRequestMessage.
 */
@Component("RequestedArtifactValidator")
class RequestedArtifactValidator extends IdsValidator<Request<ArtifactRequestMessageImpl,
        MessagePayload, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the requested artifact given in an ArtifactRequestMessage is null or empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the requested artifact is null or empty.
     */
    @Override
    protected void processInternal(final Request<ArtifactRequestMessageImpl, MessagePayload,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}
