package io.dataspaceconnector.camel.processor.controller.messagebuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.model.message.DescriptionRequestMessageDesc;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Builds a DescriptionRequestMessage and creates a request DTO with header and payload.
 */
@Component("DescriptionRequestMessageBuilder")
@RequiredArgsConstructor
public class DescriptionRequestMessageBuilder extends
        IdsMessageBuilder<DescriptionRequestMessageImpl, String> {

    /**
     * Service for description request message handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    /**
     * Builds a DescriptionRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<DescriptionRequestMessageImpl, String, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        var elementId = exchange.getProperty(ParameterUtils.ELEMENT_ID_PARAM, URI.class);
        if (elementId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            if (index != null) {
                final var resources = exchange
                        .getProperty(ParameterUtils.RESOURCES_PARAM, List.class);
                elementId = (URI) resources.get(index);
            }
        }
        final var message = (DescriptionRequestMessageImpl) descReqSvc
                .buildMessage(new DescriptionRequestMessageDesc(recipient, elementId));

        return new Request<>(message, "", Optional.empty());
    }
}
