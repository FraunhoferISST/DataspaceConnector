package io.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import io.dataspaceconnector.exceptions.MessageResponseException;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.utils.ControllerUtils;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class MessageService {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * If the response message is not of the expected type, message type, rejection reason, and the
     * payload are returned as an object.
     *
     * @param message The ids multipart message as map.
     * @return The object.
     * @throws MessageResponseException Of the response could not be read or deserialized.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Map<String, Object> getResponseContent(final Map<String, String> message)
            throws MessageResponseException, IllegalArgumentException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(message);

        final var idsMessage = deserializationService.getMessage(header);
        var responseMap = new HashMap<String, Object>() {{
            put("type", idsMessage.getClass());
        }};

        // If the message is of type exception, add the reason to the response object.
        if (idsMessage instanceof RejectionMessage) {
            final var rejectionMessage = (RejectionMessage) idsMessage;
            final var reason = MessageUtils.extractRejectionReason(rejectionMessage);
            responseMap.put("reason", reason);
        }

        responseMap.put("payload", payload);
        return responseMap;
    }

    /**
     * Extract header-payload-map from http response and return with response entity. Either with
     * status code 200 and no content or the unexpected ids message's content, or any other status
     * code if the connection attempt failed.
     *
     * @param response The http response.
     * @return The response entity.
     */
    public ResponseEntity<Object> processIdsResponse(final Response response) {
        try {
            if (!response.isSuccessful()) {
                return ControllerUtils.respondConnectionFailed(response);
            }

            final var responseToString = Objects.requireNonNull(response.body()).string();
            final var map = MultipartStringParser.stringToMultipart(responseToString);

            final var content = getResponseContent(map);
            if (!content.get("type").equals(MessageProcessedNotificationMessageImpl.class)) {
                return ControllerUtils.respondWithMessageContent(content);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FileUploadException | IllegalArgumentException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        } catch (NullPointerException | IOException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        }
    }
}
