package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import okhttp3.Response;

import java.io.IOException;

/**
 * MessageService interface.
 */
public interface MessageService {

    /**
     * Sends log message.
     *
     * @return the http response.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.io.IOException                                             if any.
     */
    Response sendLogMessage() throws HttpClientException, IOException;

    /**
     * Sends notification message.
     *
     * @return the http response.
     * @throws de.fraunhofer.isst.ids.framework.exceptions.HttpClientException if any.
     * @throws java.io.IOException                                             if any.
     */
    Response sendNotificationMessage(String recipient) throws HttpClientException, IOException;
}
