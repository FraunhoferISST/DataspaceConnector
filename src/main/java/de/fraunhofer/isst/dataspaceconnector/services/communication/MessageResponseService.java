package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.AccessTokenResponseMessage;
import de.fraunhofer.iais.eis.AppRegistrationResponseMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractResponseMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.OperationResultMessage;
import de.fraunhofer.iais.eis.ParticipantResponseMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.ResultMessage;
import de.fraunhofer.iais.eis.UploadResponseMessage;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Response;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class MessageResponseService extends MessageService{

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageResponseService.class);

    private final SerializerProvider serializerProvider;

    @Autowired
    public MessageResponseService(IDSHttpService idsHttpService, SerializerProvider serializerProvider) {
        super(idsHttpService);

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.serializerProvider = serializerProvider;
    }

    public Map<ResponseType, String> readResponse(Response response) {
        String responseAsString;
        if (response == null)
            throw new MessageResponseException("Body is empty.");

        String header, payload;
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
            serializerProvider.getSerializer().deserialize(header, ResultMessage.class);
            return ResponseType.RESULT;
        } catch (IOException ignored) { }
        try {
            serializerProvider.getSerializer().deserialize(header, UploadResponseMessage.class);
            return ResponseType.UPLOAD_RESPONSE;
        } catch (IOException ignored) { }

        return null;
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
