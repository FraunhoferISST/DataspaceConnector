package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.NoAffectedResourceException;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("AffectedResourceValidator")
public class AffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, MessagePayload>> {

    @Override
    protected void processInternal(final RouteMsg<ResourceUpdateMessageImpl, MessagePayload> message) throws Exception {
        final var affected = MessageUtils
                .extractAffectedResource(message.getHeader());

        if (affected == null || affected.toString().isEmpty()) {
            throw new NoAffectedResourceException("Affected resource is null or empty.");
        }
    }

}
