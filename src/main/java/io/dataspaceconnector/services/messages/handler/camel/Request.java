package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.Message;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link RouteMsg} interface for requests. Should contain a subclass of
 * either {@link de.fraunhofer.iais.eis.RequestMessage} or
 * {@link de.fraunhofer.iais.eis.NotificationMessage} as header and can contain an arbitrary
 * payload.
 *
 * @param <H> the header type.
 * @param <B> the body/payload type.
 */
@Data
@RequiredArgsConstructor
public class Request<H extends Message, B> implements RouteMsg<H, B> {
    /**
     * The header.
     */
    private final @NonNull H header;

    /**
     * The body/payload.
     */
    private final @NonNull B body;
}
