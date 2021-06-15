package io.dataspaceconnector.services.messages.handler.camel;

/**
 * Interface for messages exchanged between the processors in Camel routes.
 *
 * @param <H> the header type.
 * @param <B> the body/payload type.
 */
public interface RouteMsg<H, B> {
    /**
     * Returns the header of this RouteMsg.
     *
     * @return the header.
     */
    H getHeader();

    /**
     * Returns the body/payload of this RouteMsg.
     *
     * @return the body/payload.
     */
    B getBody();
}
