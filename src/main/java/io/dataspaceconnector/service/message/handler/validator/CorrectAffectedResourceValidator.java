package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.InvalidAffectedResourceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates that the correct resource ID was used in a ResourceUpdateMessage.
 */
@Component("CorrectAffectedResourceValidator")
class CorrectAffectedResourceValidator extends IdsValidator<Request<ResourceUpdateMessageImpl,
        Resource, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage matches the resource ID in
     * the message's payload.
     *
     * @param msg the incoming message.
     * @throws Exception if the IDs do not match.
     */
    @Override
    protected void processInternal(final Request<ResourceUpdateMessageImpl, Resource,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not "
                    + "match affected resource from message header.");
        }
    }

}
