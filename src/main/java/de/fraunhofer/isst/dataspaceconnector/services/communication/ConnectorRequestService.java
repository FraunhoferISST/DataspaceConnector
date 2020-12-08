package de.fraunhofer.isst.dataspaceconnector.services.communication;

import okhttp3.Response;

import java.io.IOException;
import java.net.URI;

/**
 * ConnectorRequestService interface.
 */
public interface ConnectorRequestService {

    /**
     * Sends artifact request message.
     *
     * @return  the http response.
     * @throws java.io.IOException if any.
     */
    Response sendArtifactRequestMessage(URI recipient, URI artifact) throws IOException;

    /**
     * Sends description request message.
     *
     * @return the http response.
     * @throws java.io.IOException if any.
     */
    Response sendDescriptionRequestMessage(URI recipient, URI artifact) throws IOException;

    /**
     * Sends contract request message.
     *
     * @return the http response.
     */
    Response sendContractRequestMessage();
}
