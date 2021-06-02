package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.NoRequestedArtifactException;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("RequestedArtifactValidator")
public class RequestedArtifactValidator extends IdsValidator<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    @Override
    protected void processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}
