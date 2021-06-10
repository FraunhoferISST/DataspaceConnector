package io.dataspaceconnector.services.messages.handler.camel;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Request<H, B> implements RouteMsg<H, B> {
    private final @NonNull H header;
    private final @NonNull B body;
}
