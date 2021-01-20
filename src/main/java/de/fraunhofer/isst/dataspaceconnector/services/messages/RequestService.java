package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.communication.http.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import okhttp3.MultipartBody;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Abstract class for building and sending ids messages.
 */
@Service
public abstract class RequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

    private final IDSHttpService idsHttpService;
    private final ResourceService resourceService;

    @Autowired
    public RequestService(IDSHttpService idsHttpService,
        OfferedResourceServiceImpl resourceService) throws IllegalArgumentException {
        if (idsHttpService == null)
            throw new IllegalArgumentException("The IDSHttpService cannot be null.");

        if (resourceService == null)
            throw new IllegalArgumentException("The OfferedResourceServiceImpl cannot be null.");

        this.idsHttpService = idsHttpService;
        this.resourceService = resourceService;
    }

    public abstract Message buildHeader() throws MessageException;

    public abstract URI getRecipient();

    /**
     * Send ids message with header and payload using the IDS Framework.
     *
     * @param payload The message payload.
     * @return The http response.
     * @throws MessageException - if a header could not be built or the message could not be sent.
     */
    public Map<String, String> sendMessage(String payload) throws MessageException {
        Message message;
        try {
            message = buildHeader();
        } catch (MessageBuilderException exception) {
            LOGGER.warn("Message could not be built. [exception=({})]", exception.getMessage());
            throw new MessageBuilderException("Message could not be built.", exception);
        }

        try {
            MultipartBody body = InfomodelMessageBuilder.messageWithString(message, payload);
            return idsHttpService.sendAndCheckDat(body, getRecipient());
        } catch (ClaimsException exception) {
            LOGGER.warn("Invalid DAT in incoming message. [exception=({})]" + exception.getMessage());
            throw new MessageResponseException("Unexpected message answer.", exception);
        } catch (MessageNotSentException | FileUploadException | IOException exception) {
            LOGGER.warn("Message could not be sent. [exception=({})]" + exception.getMessage());
            throw new MessageBuilderException("Message could not be sent.", exception);
        }
    }

    /**
     * Find the requested resource.
     */
    public Resource findResourceFromArtifactId(UUID artifactId) {
        for (final var resource : resourceService.getResources()) {
            for (final var representation : resource.getRepresentation()) {
                final var representationId = UUIDUtils.uuidFromUri(representation.getId());

                if (representationId.equals(artifactId)) {
                    return resource;
                }
            }
        }
        return null;
    }

    /**
     * Extracts artifact id from contract request.
     *
     * @return The artifact id.
     */
    public URI getArtifactIdFromContract(Contract request) {
        final var obligations = request.getObligation();
        final var permissions = request.getPermission();
        final var prohibitions = request.getProhibition();

        if (obligations != null && !obligations.isEmpty())
            return obligations.get(0).getTarget();

        if (permissions != null && !permissions.isEmpty())
            return permissions.get(0).getTarget();

        if (prohibitions != null && !prohibitions.isEmpty())
            return prohibitions.get(0).getTarget();

        return null;
    }
}
