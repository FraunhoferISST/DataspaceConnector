package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import io.dataspaceconnector.exceptions.InvalidAffectedResourceException;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("CorrectAffectedResourceValidator")
public class CorrectAffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    @Override
    protected void processInternal(RouteMsg<ResourceUpdateMessageImpl, Resource> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not match affected resource from message header.");
        }
    }

}
