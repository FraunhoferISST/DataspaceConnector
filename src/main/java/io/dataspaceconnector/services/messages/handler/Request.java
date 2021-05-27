package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Request {
    private final @NonNull de.fraunhofer.iais.eis.Message header;
    private final @NonNull MessagePayload body;
}
