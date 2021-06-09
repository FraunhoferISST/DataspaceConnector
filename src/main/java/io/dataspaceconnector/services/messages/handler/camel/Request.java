package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Request<H, B> implements RouteMsg<H, B> {
    private final @NonNull H header;
    private final @NonNull B body;
}


class IdsRequest<H extends de.fraunhofer.iais.eis.Message, B> extends Request<H , B>{
    public IdsRequest(final H header, final B body) {
        super(header, body);
    }
}

class IdsRawRequest<H extends Message> extends IdsRequest<H, MessagePayload> {
    public IdsRawRequest(final H header, final MessagePayload body) {
        super(header, body);
    }
}
