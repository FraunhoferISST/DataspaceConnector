package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.model.message.ArtifactRequestMessageDesc;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import io.dataspaceconnector.util.QueryInput;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Builds an ArtifactRequestMessage and creates a request DTO with header and payload.
 */
@Component("ArtifactRequestMessageBuilder")
@RequiredArgsConstructor
public class ArtifactRequestMessageBuilder
        extends IdsMessageBuilder<ArtifactRequestMessageImpl, QueryInput> {

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Builds an ArtifactRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ArtifactRequestMessageImpl, QueryInput, Optional<Jws<Claims>>>
    processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var agreementId = exchange
                .getProperty(ParameterUtils.TRANSFER_CONTRACT_PARAM, URI.class);
        final var queryInput = exchange
                .getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class);

        URI artifactId = exchange.getProperty(ParameterUtils.ARTIFACT_ID_PARAM, URI.class);
        if (artifactId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            final var artifacts = exchange
                    .getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);
            artifactId = (URI) artifacts.get(index);
        }

        final var message = (ArtifactRequestMessageImpl) artifactReqSvc
                .buildMessage(new ArtifactRequestMessageDesc(recipient, artifactId, agreementId));

        return new Request<>(message, queryInput, Optional.empty());
    }

}
