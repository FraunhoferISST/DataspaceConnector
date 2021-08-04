package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.NoAffectedResourceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates that the resource ID given in the header of a ResourceUpdateMessage is not null or
 * empty.
 */
@Component("AffectedResourceValidator")
class AffectedResourceValidator extends IdsValidator<Request<ResourceUpdateMessageImpl,
        MessagePayload, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage is null or empty.
     *
     * @param message the incoming message.
     * @throws Exception if the ID is null or empty.
     */
    @Override
    protected void processInternal(final Request<ResourceUpdateMessageImpl,
            MessagePayload, Optional<Jws<Claims>>> message) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(message.getHeader());
        if (affected == null || affected.toString().isEmpty()) {
            throw new NoAffectedResourceException("Affected resource is null or empty.");
        }
    }

}
