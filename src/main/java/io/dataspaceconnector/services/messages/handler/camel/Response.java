package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.Message;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link RouteMsg} interface for responses. Should contain a subclass of
 * either {@link de.fraunhofer.iais.eis.ResponseMessage} or
 * {@link de.fraunhofer.iais.eis.NotificationMessage} as header.
 *
 */
@Data
@RequiredArgsConstructor
public class Response implements RouteMsg<Message, String> {
    /**
     * The header.
     */
    private final @NonNull Message header;

    /**
     * The body/payload.
     */
    private final @NonNull String body;
}
