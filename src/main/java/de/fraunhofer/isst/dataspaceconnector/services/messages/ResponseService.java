package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import okhttp3.Response;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public abstract class ResponseService extends RequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResponseService.class);

    private final SerializerProvider serializerProvider;
    private final IdsUtils idsUtils;
    private String header;

    @Autowired
    public ResponseService(IDSHttpService idsHttpService, IdsUtils idsUtils,
        SerializerProvider serializerProvider, OfferedResourceServiceImpl resourceService)
        throws IllegalArgumentException {
        super(idsHttpService, resourceService);

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.serializerProvider = serializerProvider;
        this.idsUtils = idsUtils;
    }

    /**
     * Checks if the outbound model version of the requesting connector is listed in the inbound model versions.
     *
     * @param versionString The outbound model version of the requesting connector.
     * @return True on no hit, hence incompatibility.
     */
    public boolean versionSupported(String versionString) throws ConnectorConfigurationException {
        final var connector = idsUtils.getConnector();

        for (var version : connector.getInboundModelVersion()) {
            if (version.equals(versionString)) {
                return true;
            }
        }
        return false;
    }

    public Map<ResponseType, String> handleResponse(Response response) throws MessageException {
        String responseAsString;
        if (response == null)
            throw new MessageResponseException("Body is empty.");

        String payload;
        try {
            responseAsString = response.body().string();

            Map<String, String> map = MultipartStringParser.stringToMultipart(responseAsString);
            header = map.get("header");
            payload = map.get("payload");
        } catch (IOException | FileUploadException e) {
            throw new MessageResponseException("Body could not be parsed.", e);
        }

        return new HashMap<>() {{ put(getResponseType(header), payload); }};
    }

    private ResponseType getResponseType(String header) {
        try {
            serializerProvider.getSerializer().deserialize(header, AccessTokenResponseMessage.class);
            return ResponseType.ACCESS_TOKEN_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, AppRegistrationResponseMessage.class);
            return ResponseType.APP_REGISTRATION_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ArtifactResponseMessage.class);
            return ResponseType.ARTIFACT_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ContractAgreementMessage.class);
            return ResponseType.CONTRACT_AGREEMENT;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ContractResponseMessage.class);
            return ResponseType.CONTRACT_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, DescriptionResponseMessage.class);
            return ResponseType.DESCRIPTION_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, OperationResultMessage.class);
            return ResponseType.OPERATION_RESULT;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ParticipantResponseMessage.class);
            return ResponseType.PARTICIPANT_RESPONSE;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, RejectionMessage.class);
            return ResponseType.REJECTION;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ContractRejectionMessage.class);
            return ResponseType.CONTRACT_REJECTION;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, ResultMessage.class);
            return ResponseType.RESULT;
        } catch (IOException ignored) { }

        try {
            serializerProvider.getSerializer().deserialize(header, UploadResponseMessage.class);
            return ResponseType.UPLOAD_RESPONSE;
        } catch (IOException ignored) { }

        return null;
    }

    public String getHeader() {
        return header;
    }

    public enum ResponseType {
        ACCESS_TOKEN_RESPONSE("ACCESS_TOKEN_RESPONSE"),
        APP_REGISTRATION_RESPONSE("APP_REGISTRATION_RESPONSE"),
        ARTIFACT_RESPONSE("ARTIFACT_RESPONSE"),
        CONTRACT_AGREEMENT("CONTRACT_AGREEMENT"),
        CONTRACT_RESPONSE("CONTRACT_RESPONSE"),
        DESCRIPTION_RESPONSE("DESCRIPTION_RESPONSE"),
        OPERATION_RESULT("OPERATION_RESULT"),
        PARTICIPANT_RESPONSE("PARTICIPANT_RESPONSE"),
        REJECTION("REJECTION"),
        CONTRACT_REJECTION("CONTRACT_REJECTION"),
        RESULT("RESULT"),
        UPLOAD_RESPONSE("UPLOAD_RESPONSE");

        private final String type;

        ResponseType(String string) {
            type = string;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
