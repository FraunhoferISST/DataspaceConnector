package de.fraunhofer.isst.dataspaceconnector.controller;

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
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/broker")
@Tag(name = "Connector: IDS Broker Communication",
    description = "Endpoints for invoking broker communication")
public class BrokerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

    private final TokenProvider tokenProvider;
    private final BrokerService brokerService;
    private final OfferedResourceService offeredResourceService;

    /**
     * Constructor
     *
     * @param tokenProvider          a {@link TokenProvider} object.
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer}
     *                               object.
     * @param offeredResourceService a {@link OfferedResourceService} object.
     * @throws IllegalArgumentException - if any of the parameters is null.
     * @throws GeneralSecurityException - if the framework has an error.
     */
    @Autowired
    public BrokerController(@NotNull TokenProvider tokenProvider,
        @NotNull ConfigurationContainer configurationContainer,
        @NotNull OfferedResourceService offeredResourceService)
        throws IllegalArgumentException, GeneralSecurityException {
        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (tokenProvider == null) {
            throw new IllegalArgumentException("The TokenProvider cannot be null.");
        }

        if (configurationContainer == null) {
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");
        }

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
                    + brokerResponse.body().string(),
                    HttpStatus.OK);
            } catch (NullPointerException | IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
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
                return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
            } catch (NullPointerException | IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
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
    public ResponseEntity<String> queryBroker(
        @Parameter(description = "The url of the broker.",
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
                final var brokerResponse = brokerService.queryBroker(url, query,
                    null, null, null);
                return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
            } catch (IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
        }
    }

    /**
     * Sends a ResourceUpdateMessage to an IDS broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request",
        description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/resource/{resource-id}/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(brokerService, "The brokerService cannot be null.");
        Assert.notNull(offeredResourceService, "The offeredResourceService cannot be null.");

        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Get the resource
                final var resource =
                    offeredResourceService.getOfferedResources().get(resourceId);
                if (resource == null) {
                    // The resource could not be found, reject and inform the requester
                    return respondResourceNotFound(resourceId);
                } else {
                    // The resource has been received, update at broker
                    final var brokerResponse =
                        brokerService.updateResourceAtBroker(url, resource);
                    return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
                }
            } catch (ClassCastException | NullPointerException exception) {
                // An (implementation) error occurred while receiving the resource
                LOGGER.error("Resource not be loaded.");
                return new ResponseEntity<>("Could not load resource.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
        }
    }

    /**
     * Sends a ResourceUpdateMessage to an IDS broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request",
        description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/update/{resource-id}/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(brokerService, "The brokerService cannot be null.");
        Assert.notNull(offeredResourceService, "The offeredResourceService cannot be null.");

        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Get the resource
                final var resource =
                    offeredResourceService.getOfferedResources().get(resourceId);
                if (resource == null) {
                    // The resource could not be found, reject and inform the requester
                    return respondResourceNotFound(resourceId);
                } else {
                    // The resource has been received, remove from broker
                    final var brokerResponse =
                        brokerService.removeResourceFromBroker(url, resource);
                    return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
                }
            } catch (ClassCastException | NullPointerException exception) {
                // An (implementation) error occurred while receiving the resource
                LOGGER.error("Resource not be loaded.");
                return new ResponseEntity<>("Could not load resource.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
        }
    }

    private ResponseEntity<String> respondResourceNotFound(UUID resourceId) {
        // The resource could not be found, reject and inform the requester
        LOGGER.info(String.format("Resource update failed. Resource %s could not be found.",
            resourceId));
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<String> respondBrokerCommunicationFailed(Exception exception) {
        // The broker could not be reached.
        LOGGER.info("Broker communication failed: " + exception.getMessage());

        return new ResponseEntity<>("The communication with the broker failed.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> respondRejectUnauthorized(String url) {
        // The request was unauthorized.
        LOGGER.warn("Unauthorized call. No DAT token found. Tried call with url:" + url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }
}
