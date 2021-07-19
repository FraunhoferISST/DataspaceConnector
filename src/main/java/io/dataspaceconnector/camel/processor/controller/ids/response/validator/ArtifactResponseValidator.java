package io.dataspaceconnector.camel.processor.controller.ids.response.validator;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ProcessorUtils;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates the response to an ArtifactRequestMessage.
 */
@Component("ArtifactResponseValidator")
@RequiredArgsConstructor
public class ArtifactResponseValidator extends IdsResponseMessageValidator {

    /**
     * Service for ArtifactRequestMessage handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Validates the response to an ArtifactRequestMessage.
     * @param response the response DTO.
     * @throws MessageResponseException if the received response is not valid.
     */
    @Override
    protected void processInternal(final Response response) throws MessageResponseException {
        final var map = ProcessorUtils.getResponseMap(response);

        if (!artifactReqSvc.validateResponse(map)) {
            // If the response is not a description response message, show the response.
            final var content = artifactReqSvc.getResponseContent(map);
            throw new InvalidResponseException(content, ERROR_MESSAGE);
        }
    }
}
