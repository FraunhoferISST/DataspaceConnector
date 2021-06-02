package io.dataspaceconnector.services.messages.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.InvalidInputException;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.messages.ArtifactResponseMessageDesc;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.messages.types.ArtifactResponseService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Log4j2
@Component("DataRequestProcessor")
@RequiredArgsConstructor
public class DataRequestProcessor extends IdsProcessor<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling artifact response messages.
     */
    private final @NonNull ArtifactResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    @Override
    protected Response processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var requestedArtifact = msg.getHeader().getRequestedArtifact();
        final var issuer = msg.getHeader().getIssuerConnector();
        final var messageId = msg.getHeader().getId();
        final var transferContract = msg.getHeader().getTransferContract();

        final var queryInput = getQueryInputFromPayload(msg.getBody());
        final var data = entityResolver.getDataByArtifactId(requestedArtifact, queryInput);

        final var desc = new ArtifactResponseMessageDesc(issuer, messageId, transferContract);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, Base64Utils.encodeToString(data.readAllBytes()));
    }

    /**
     * Read query parameters from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the query input.
     * @throws InvalidInputException If the query input is not empty but invalid.
     */
    private QueryInput getQueryInputFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("")) {
                // Query input is optional, so no rejection message will be sent. Query input will
                // be checked for null value in HttpService.class.
                return null;
            } else {
                return new ObjectMapper().readValue(payload, QueryInput.class);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid query input. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid query input.", e);
        }
    }

}
