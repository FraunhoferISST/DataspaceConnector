// package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.boot.test.mock.mockito.SpyBean;

// import java.net.URI;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.GregorianCalendar;

// import javax.xml.datatype.DatatypeConfigurationException;
// import javax.xml.datatype.DatatypeFactory;

// import de.fraunhofer.iais.eis.BaseConnectorBuilder;
// import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
// import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
// import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
// import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
// import de.fraunhofer.iais.eis.RejectionReason;
// import de.fraunhofer.iais.eis.SecurityProfile;
// import de.fraunhofer.iais.eis.TokenFormat;
// import de.fraunhofer.iais.eis.util.Util;
// import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
// import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
// import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
// import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
// import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
// import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
// import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;

// @SpringBootTest
// class DescriptionRequestHandlerTest {
//     @SpyBean
//     ConnectorService connectorService;

//     @MockBean
//     EntityResolver resolver;

//     @Autowired
//     DescriptionRequestHandler handler;

//     @Test
//     public void handleMessage_validSelfDescriptionMsg_returnSelfDescription()
//             throws DatatypeConfigurationException {
//         /* ARRANGE */
//         final var connector = new BaseConnectorBuilder()
//                                       ._resourceCatalog_(new ArrayList<>())
//                                       ._outboundModelVersion_("4.0.0")
//                                       ._inboundModelVersion_(Util.asList("4.0.0"))
//                                       ._maintainer_(URI.create("https://someMaintainer"))
//                                       ._curator_(URI.create("https://someCurator"))
//                                       ._hasDefaultEndpoint_(new ConnectorEndpointBuilder(
//                                           URI.create("https://someEndpoint"))
//                                       ._accessURL_(URI.create("https://someAccessUrl"))
//                                       .build())
//                                       ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
//                                       .build();

//         final var calendar = new GregorianCalendar();
//         calendar.setTime(new Date());
//         final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

//         final var message = new DescriptionRequestMessageBuilder()
//                                     ._senderAgent_(URI.create("https://localhost:8080"))
//                                     ._issuerConnector_(URI.create("https://localhost:8080"))
//                                     ._securityToken_(new DynamicAttributeTokenBuilder()
//                                                              ._tokenFormat_(TokenFormat.OTHER)
//                                                              ._tokenValue_("")
//                                                              .build())
//                                     ._modelVersion_("4.0.0")
//                                     ._issued_(xmlCalendar)
//                                     .build();

//         Mockito.doReturn(connector).when(connectorService).getConnectorWithOfferedResources();

//         /* ACT */
//         final var result =
//                 (BodyResponse) handler.handleMessage((DescriptionRequestMessageImpl) message, null);

//         /* ASSERT */
//         final var expected = (BodyResponse) handler.constructSelfDescription(
//                 message.getIssuerConnector(), message.getId());

//         // Compare payload
//         assertEquals(expected.getPayload(), result.getPayload());

//         // Compare header
//         assertEquals(
//                 expected.getHeader().getIssuerConnector(), result.getHeader().getIssuerConnector());
//         assertEquals(expected.getHeader().getAuthorizationToken(),
//                 result.getHeader().getAuthorizationToken());
//         assertEquals(expected.getHeader().getComment().toString(),
//                 result.getHeader().getComment().toString());
//         assertEquals(expected.getHeader().getCorrelationMessage(),
//                 result.getHeader().getCorrelationMessage());
//         assertEquals(
//                 expected.getHeader().getContentVersion(), result.getHeader().getContentVersion());
//         assertEquals(expected.getHeader().getLabel().toString(),
//                 result.getHeader().getLabel().toString());
//         assertEquals(expected.getHeader().getModelVersion(), result.getHeader().getModelVersion());
//         assertEquals(expected.getHeader().getProperties(), result.getHeader().getProperties());
//         assertEquals(
//                 expected.getHeader().getRecipientAgent(), result.getHeader().getRecipientAgent());
//         assertEquals(expected.getHeader().getRecipientConnector(),
//                 result.getHeader().getRecipientConnector());
//         assertEquals(expected.getHeader().getSenderAgent(), result.getHeader().getSenderAgent());
//         assertEquals(expected.getHeader().getTransferContract(),
//                 result.getHeader().getTransferContract());
//     }

//     @Test
//     public void handleMessage_validResourceDescriptionMsgKnownId_returnResourceDescription()
//             throws DatatypeConfigurationException {
//         /* ARRANGE */
//         final var artifact = new ArtifactFactory().create(new ArtifactDesc());

//         final var calendar = new GregorianCalendar();
//         calendar.setTime(new Date());
//         final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

//         final var message =
//                 new DescriptionRequestMessageBuilder()
//                         ._senderAgent_(URI.create("https://localhost:8080"))
//                         ._issuerConnector_(URI.create("https://localhost:8080"))
//                         ._securityToken_(new DynamicAttributeTokenBuilder()
//                                                  ._tokenFormat_(TokenFormat.OTHER)
//                                                  ._tokenValue_("")
//                                                  .build())
//                         ._modelVersion_("4.0.0")
//                         ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
//                         ._issued_(xmlCalendar)
//                         .build();

//         Mockito.when(resolver.getEntityById(Mockito.eq(message.getRequestedElement())))
//                 .thenReturn(artifact);

//         /* ACT */
//         final var result =
//                 (BodyResponse) handler.handleMessage((DescriptionRequestMessageImpl) message, null);

//         /* ASSERT */
//         final var expected = (BodyResponse) handler.constructResourceDescription(
//                 message.getRequestedElement(), message.getIssuerConnector(), message.getId());

//         // Compare payload
//         assertEquals(expected.getPayload(), result.getPayload());

//         // Compare header
//         assertEquals(
//                 expected.getHeader().getIssuerConnector(), result.getHeader().getIssuerConnector());
//         assertEquals(expected.getHeader().getAuthorizationToken(),
//                 result.getHeader().getAuthorizationToken());
//         assertEquals(expected.getHeader().getComment().toString(),
//                 result.getHeader().getComment().toString());
//         assertEquals(expected.getHeader().getCorrelationMessage(),
//                 result.getHeader().getCorrelationMessage());
//         assertEquals(
//                 expected.getHeader().getContentVersion(), result.getHeader().getContentVersion());
//         assertEquals(expected.getHeader().getLabel().toString(),
//                 result.getHeader().getLabel().toString());
//         assertEquals(expected.getHeader().getModelVersion(), result.getHeader().getModelVersion());
//         assertEquals(expected.getHeader().getProperties(), result.getHeader().getProperties());
//         assertEquals(
//                 expected.getHeader().getRecipientAgent(), result.getHeader().getRecipientAgent());
//         assertEquals(expected.getHeader().getRecipientConnector(),
//                 result.getHeader().getRecipientConnector());
//         assertEquals(expected.getHeader().getSenderAgent(), result.getHeader().getSenderAgent());
//         assertEquals(expected.getHeader().getTransferContract(),
//                 result.getHeader().getTransferContract());
//     }

//     @Test
//     public void handleMessage_nullMessage_returnBadParametersRejectionMessage() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT */
//         final var result = (ErrorResponse) handler.handleMessage(null, null);

//         /* ASSERT */
//         assertEquals(
//                 RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
//     }

//     @Test
//     public void handleMessage_unsupportedMessage_returnUnsupportedVersionRejectionMessage()
//             throws DatatypeConfigurationException {
//         /* ARRANGE */
//         final var calendar = new GregorianCalendar();
//         calendar.setTime(new Date());
//         final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

//         final var message =
//                 new DescriptionRequestMessageBuilder()
//                         ._senderAgent_(URI.create("https://localhost:8080"))
//                         ._issuerConnector_(URI.create("https://localhost:8080"))
//                         ._securityToken_(new DynamicAttributeTokenBuilder()
//                                                  ._tokenFormat_(TokenFormat.OTHER)
//                                                  ._tokenValue_("")
//                                                  .build())
//                         ._modelVersion_("tetris")
//                         ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
//                         ._issued_(xmlCalendar)
//                         .build();

//         /* ACT */
//         final var result = (ErrorResponse) handler.handleMessage(
//                 (DescriptionRequestMessageImpl) message, null);

//         /* ASSERT */
//         assertEquals(RejectionReason.VERSION_NOT_SUPPORTED,
//                 result.getRejectionMessage().getRejectionReason());
//     }

//     @Test
//     public void handleMessage_validResourceDescriptionMsgUnknownId_returnNotFoundRejectionReason()
//             throws DatatypeConfigurationException {
//         /* ARRANGE */
//         final var calendar = new GregorianCalendar();
//         calendar.setTime(new Date());
//         final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

//         final var message =
//                 new DescriptionRequestMessageBuilder()
//                         ._senderAgent_(URI.create("https://localhost:8080"))
//                         ._issuerConnector_(URI.create("https://localhost:8080"))
//                         ._securityToken_(new DynamicAttributeTokenBuilder()
//                                                  ._tokenFormat_(TokenFormat.OTHER)
//                                                  ._tokenValue_("")
//                                                  .build())
//                         ._modelVersion_("4.0.0")
//                         ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
//                         ._issued_(xmlCalendar)
//                         .build();

//         Mockito.when(resolver.getEntityById(Mockito.eq(message.getRequestedElement())))
//                 .thenThrow(ResourceNotFoundException.class);

//         /* ACT */
//         final var result = (ErrorResponse) handler.handleMessage(
//                 (DescriptionRequestMessageImpl) message, null);

//         /* ASSERT */
//         assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
//     }
// }
