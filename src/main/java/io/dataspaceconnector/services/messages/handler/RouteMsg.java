package io.dataspaceconnector.services.messages.handler;

public interface RouteMsg<H, B> {
    H getHeader();
    B getBody();
}
