package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.BrokerService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import io.jsonwebtoken.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class provides endpoints for the communication with an IDS broker instance.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/broker")
@Tag(name = "Connector: IDS Broker Communication", description = "Endpoints for invoking broker communication")
public class BrokerController {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

    private TokenProvider tokenProvider;
    private BrokerService brokerService;
    private OfferedResourceService offeredResourceService;

    /**
     * <p>Constructor for BrokerController.</p>
     *
     * @param tokenProvider          a {@link TokenProvider} object.
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param offeredResourceService a {@link OfferedResourceService} object.
     * @throws IllegalArgumentException - if any of the parameters is null.
     * @throws GeneralSecurityException - if the framework has an error.
     */
    @Autowired
    public BrokerController(@NotNull TokenProvider tokenProvider,
                            @NotNull ConfigurationContainer configurationContainer,
                            @NotNull OfferedResourceService offeredResourceService) throws IllegalArgumentException, GeneralSecurityException {
        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.tokenProvider = tokenProvider;
        this.offeredResourceService = offeredResourceService;

        try {
            this.brokerService = new BrokerService(configurationContainer,
                    new ClientProvider(configurationContainer), tokenProvider);
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            LOGGER.error("Failed to initialize the broker. Error in the framework.", exception);
            throw new GeneralSecurityException("Error in the framework.", exception);
        }
    }

    /**
     * Sends a ConnectorAvailableMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Register Connector",
            description = "Register or update connector at an IDS broker.")
    @RequestMapping(value = {"/register", "/update"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAtBroker(@Parameter(description = "The url of the broker."
            , required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
                                                 @RequestParam("broker") String url) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(brokerService, "The brokerService cannot be null.");

        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Send the update request to the broker
                final var brokerResponse = brokerService.updateAtBroker(url);
                return new ResponseEntity<>("The broker answered with: "
                        + brokerResponse.body().toString(),
                        HttpStatus.OK);
            } catch (NullPointerException | IOException exception) {
                return brokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return rejectUnauthorized(url);
        }
    }

    /**
     * Sends a ConnectorUnavailableMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Unregister Connector", description = "Unregister connector at an IDS broker.")
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unregisterAtBroker(
            @Parameter(description = "The url of the broker.",
            required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
                                                         @RequestParam("broker") String url) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(brokerService, "The brokerService cannot be null.");

        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Send the unregister request to the broker
                final var brokerResponse = brokerService.unregisterAtBroker(url);
                return new ResponseEntity<>(brokerResponse.body().toString(), HttpStatus.OK);
            } catch (NullPointerException | IOException exception) {
                return brokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return rejectUnauthorized(url);
        }
    }

    /**
     * Sends a QueryMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request", description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> queryBroker(@Parameter(description = "The url of the broker.",
            required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
                                                  @RequestParam("broker") String url) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(brokerService, "The brokerService cannot be null.");

        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            // Send the query request to the broker
            final var query = "SELECT ?subject ?predicate ?object\n" +
                    "FROM <urn:x-arq:UnionGraph>\n" +
                    "WHERE {\n" +
                    "  ?subject ?predicate ?object\n" +
                    "};";

            try {
                final var brokerReponse = brokerService.queryBroker(url, query,
                        null, null, null);
                return new ResponseEntity<>(brokerReponse.body().string(), HttpStatus.OK);
            } catch (IOException exception) {
                return brokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return rejectUnauthorized(url);
        }
    }

    /**
     * Sends a ResourceUpdateMessage to an IDS broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request", description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/resource/{resource-id}/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> updateResourceAtBroker(@Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure") @RequestParam("broker") String url,
                                                         @Parameter(description = "The resource id.", required = true) @PathVariable("resource-id") UUID resourceId) {
        if (tokenProvider.getTokenJWS() != null) {
            Resource resource;
            try {
                resource = offeredResourceService.getOfferedResources().get(resourceId);
            } catch (Exception e) {
                LOGGER.error("Resource could not be found: {}", e.getMessage());
                return new ResponseEntity<>("Resource not found.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                return new ResponseEntity<>(brokerService.updateResourceAtBroker(url, resource).body().string(), HttpStatus.OK);
            } catch (IOException e) {
                LOGGER.error("Broker communication failed: " + e.getMessage());
                return new ResponseEntity<>("Broker communication failed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.error("No DAT token found");
            return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Sends a ResourceUpdateMessage to an IDS broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request", description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/update/{resource-id}/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> deleteResourceAtBroker(@Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure") @RequestParam("broker") String url,
                                                         @Parameter(description = "The resource id.", required = true) @PathVariable("resource-id") UUID resourceId) {
        if (tokenProvider.getTokenJWS() != null) {
            Resource resource;
            try {
                resource = offeredResourceService.getOfferedResources().get(resourceId);
            } catch (Exception e) {
                LOGGER.error("Resource could not be found: {}", e.getMessage());
                return new ResponseEntity<>("Resource not found.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                return new ResponseEntity<>(brokerService.removeResourceFromBroker(url, resource).body().string(), HttpStatus.OK);
            } catch (IOException e) {
                LOGGER.error("Broker communication failed: " + e.getMessage());
                return new ResponseEntity<>("Broker communication failed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.error("No DAT token found");
            return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<String> brokerCommunicationFailed(Exception exception) {
        // The broker could not be reached.
        LOGGER.info("Broker communication failed: " + exception.getMessage());

        return new ResponseEntity<>("The communication with the broker failed.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> rejectUnauthorized(String url){
        // The request was unauthorized.
        LOGGER.warn("Unauthorized call. No DAT token found. Tried call with url:" + url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }
}
