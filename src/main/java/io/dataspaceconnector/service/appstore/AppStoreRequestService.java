/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.appstore;

import de.fraunhofer.iais.eis.AppResource;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParser;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import io.dataspaceconnector.util.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Service for handling requests to the AppStore (parsing of responses, downloading of apps...).
 */
@Service
@AllArgsConstructor
public class AppStoreRequestService {

    /**
     * Service for message handling.
     */
    private final @NonNull DescriptionRequestService descriptionReqSvc;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationSvc;

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Service for handle application.properties settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Send an AppDescriptionRequestMessage and parse the response to AppResource.
     *
     * @param recipient app store location.
     * @param app requested app id.
     * @return AppResource parsed from AppStore response.
     */
    public AppResource sendAppDescriptionRequestMessage(final URI recipient, final URI app) {
        String payload = null;
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:descriptionRequestSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .withProperty(ParameterUtils.ELEMENT_ID_PARAM, app)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                payload = response.getBody();
                try {
                    var multipartMap = MultipartParser.stringToMultipart(payload);
                    payload = MessageUtils.extractPayloadFromMultipartMessage(multipartMap);
                } catch (MultipartParseException e) {
                    //TODO catch handle
                }
            }
        } else {
            try {
                // Send and validate description request/response message.
                final var response = descriptionReqSvc.sendMessage(recipient, app);

                // Read and process the response message.
                payload = MessageUtils.extractPayloadFromMultipartMessage(response);
            } catch (UnexpectedResponseException e) {
                //TODO catch handle
            }
        }
        if (payload != null) {
            try {
                return deserializationSvc.getAppResource(payload);
            } catch (IllegalArgumentException e) {
                //TODO resource cannot be parsed
                return null;
            }
        } else {
            //TODO no response/no parsed appresource
            return null;
        }
    }

    /**
     * Send an AppArtifactRequestMessage and parse the response to AppTemplate.
     *
     * @param location App store location.
     * @param artifactId Requested Artifact ID.
     * @return AppTemplate response from AppStore.
     */
    public String sendAppArtifactRequestMessage(final URI location, final URI artifactId) {
        //TODO send an app artifact request message, get App Template from response
        String payload = null;
        if (connectorConfig.isIdscpEnabled()) {
            final var result = template.send("direct:artifactRequestSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, location)
                            .withProperty(ParameterUtils.ARTIFACT_ID_PARAM, artifactId)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                payload = response.getBody();
                try {
                    var multipartMap = MultipartParser.stringToMultipart(payload);
                    payload = MessageUtils.extractPayloadFromMultipartMessage(multipartMap);
                } catch (MultipartParseException e) {
                    //TODO catch handle
                }
            }
        } else {
            try {
                // Send and validate description request/response message.
                final var response = descriptionReqSvc.sendMessage(
                        location,
                        artifactId
                );

                // Read and process the response message.
                payload = MessageUtils.extractPayloadFromMultipartMessage(response);
            } catch (UnexpectedResponseException e) {
                //TODO catch handle
            }
        }
        return payload;
    }

    /**
     * Download an app image from the appstore.
     *
     * @param recipient app registry location.
     * @param appResource app resource to download.
     */
    private void downloadApp(final URI recipient, final URI appResource) {
        //TODO apps should be downloaded when they are started, not after the request
//        metadataDownloader.downloadAppResource(recipient, appResource);
    }

}
