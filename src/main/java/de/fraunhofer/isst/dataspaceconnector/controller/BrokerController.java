package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.spring.starter.BrokerService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints for the communication with an IDS broker instance.
 */
@RestController
@RequestMapping("/admin/api/broker")
@Tag(name = "Connector: IDS Broker Communication",
    description = "Endpoints for invoking broker communication")
public class BrokerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

    private final TokenProvider tokenProvider;
    private final BrokerService brokerService;
    private final ResourceService resourceService;
    private final ConfigurationContainer configurationContainer;

    /**
     * Constructor for BrokerController.
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     * @throws GeneralSecurityException - if the framework has an error.
     */
    @Autowired
    public BrokerController(TokenProvider tokenProvider,
        ConfigurationContainer configurationContainer,
        OfferedResourceServiceImpl offeredResourceService)
        throws IllegalArgumentException, GeneralSecurityException {
        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.tokenProvider = tokenProvider;
        this.resourceService = offeredResourceService;
        this.configurationContainer = configurationContainer;

        try {
            this.brokerService = new BrokerService(configurationContainer,
                new ClientProvider(configurationContainer), tokenProvider);
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            LOGGER.error("Failed to initialize the broker. Error in the framework. "
                    + "[exception=({})]", exception.getMessage());
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
        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                updateConfigModel();
                // Send the update request to the broker
                final var brokerResponse = brokerService.updateAtBroker(url);
                return new ResponseEntity<>("The broker answered with: "
                    + brokerResponse.body().string(),
                    HttpStatus.OK);
            } catch (ConfigurationUpdateException e) {
                return respondUpdateError(url);
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
        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                updateConfigModel();
                // Send the unregister request to the broker
                final var brokerResponse = brokerService.unregisterAtBroker(url);
                return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
            } catch (ConfigurationUpdateException e) {
                return respondUpdateError(url);
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
        @RequestParam("broker") String url,
        @Schema(description = "Database query (SparQL)", required = true,
            example = "SELECT ?subject ?predicate ?object\n" +
                "FROM <urn:x-arq:UnionGraph>\n" +
                "WHERE {\n" +
                "  ?subject ?predicate ?object\n" +
                "};") @RequestBody String query) {
        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            // Send the query request to the broker
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
    @Operation(summary = "Update Resource at Broker",
        description = "Update an IDS resource at an IDS broker.")
    @RequestMapping(value = "/update/{resource-id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Get the resource
                final var resource =
                    ((OfferedResourceServiceImpl) resourceService).getOfferedResources().get(resourceId);
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
                LOGGER.error("Resource not loaded. [exception=({})]", exception.getMessage());
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
     * Sends a ResourceUnvailableMessage to an IDS broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Remove Resource from Broker",
        description = "Remove an IDS resource at an IDS broker.")
    @RequestMapping(value = "/remove/{resource-id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getTokenJWS() != null) {
            try {
                // Get the resource
                final var resource =
                    ((OfferedResourceServiceImpl) resourceService).getOfferedResources().get(resourceId);
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
                LOGGER.error("Resource not loaded. [exception=({})]", exception.getMessage());
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

    private void updateConfigModel() throws ConfigurationUpdateException {
        BaseConnectorImpl connector = (BaseConnectorImpl) configurationContainer.getConnector();
        connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
            ._offeredResource_((ArrayList<Resource>) resourceService.getResources())
            .build()));

        ConfigurationModelImpl configurationModel = (ConfigurationModelImpl) configurationContainer.getConfigModel();
        configurationModel.setConnectorDescription(connector);

        configurationContainer.updateConfiguration(configurationModel);
    }

    /**
     * If the resource could not be found, reject and inform the requester.
     */
    private ResponseEntity<String> respondResourceNotFound(UUID resourceId) {
        LOGGER.debug("Resource update failed. Resource not be found. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }

    /**
     * The broker could not be reached.
     */
    private ResponseEntity<String> respondBrokerCommunicationFailed(Exception exception) {
        LOGGER.debug("Broker communication failed. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("The communication with the broker failed.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * The request was unauthorized.
     */
    private ResponseEntity<String> respondRejectUnauthorized(String url) {
        LOGGER.debug("Unauthorized call. No DAT token found. [url=({})]", url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * If the configuration/connector could not be updated.
     */
    private ResponseEntity<String> respondUpdateError(String url) {
        LOGGER.debug("Configuration error. Could not build current connector. [url=({})]", url);
        return new ResponseEntity<>("Configuration error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
