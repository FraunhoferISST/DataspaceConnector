package io.dataspaceconnector.services.messages.handler.camel;

public interface RouteMsg<H, B> {
    H getHeader();
    B getBody();
}
