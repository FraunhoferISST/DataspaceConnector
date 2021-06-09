package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.Message;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Response implements RouteMsg<Message, String> {
    private final @NonNull Message header;
    private final @NonNull String body;
}
