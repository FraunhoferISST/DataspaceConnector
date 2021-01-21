package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static de.fraunhofer.isst.dataspaceconnector.services.utils.ControllerUtils.*;

/**
 * This class provides endpoints for the communication with an IDS broker instance.
 */
@RestController
@RequestMapping("/admin/api/broker")
@Tag(name = "Connector: IDS Broker Communication",
    description = "Endpoints for invoking broker communication")
public class BrokerController {

    private final DapsTokenProvider tokenProvider;
    private final IDSBrokerService brokerService;
    private final ResourceService resourceService;
    private final ConfigurationContainer configurationContainer;

    /**
     * Constructor for BrokerController.
     *
     * @param tokenProvider The token provider
     * @param configurationContainer The container with the configuration
     * @param offeredResourceService The service for the offered resources
     * @param brokerService The service for the broker
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public BrokerController(DapsTokenProvider tokenProvider,
        ConfigurationContainer configurationContainer,
        OfferedResourceServiceImpl offeredResourceService,
        IDSBrokerService brokerService)
        throws IllegalArgumentException {
        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (brokerService == null)
            throw new IllegalArgumentException("The IDSBrokerService cannot be null.");

        this.tokenProvider = tokenProvider;
        this.resourceService = offeredResourceService;
        this.configurationContainer = configurationContainer;
        this.brokerService = brokerService;
    }

    /**
     * Notify an IDS broker of the availability of this connector.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Register Connector",
        description = "Register or update connector at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = {"/register", "/update"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAtBroker(@Parameter(description = "The url of the broker."
        , required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
    @RequestParam("broker") String url) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
            try {
                updateConfigModel();
                // Send the update request to the broker.
                final var brokerResponse = brokerService.updateSelfDescriptionAtBroker(url);
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
     * Notify an IDS broker that this connector is no longer available.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Unregister Connector", description = "Unregister connector at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unregisterAtBroker(
        @Parameter(description = "The url of the broker.",
            required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
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
     * Pass a query message to an ids broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request", description = "Send a query request to an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
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
        if (tokenProvider.getDAT() != null) {
            // Send the query request to the broker.
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
     * Update a resource at an ids broker.
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Update Resource at Broker",
        description = "Update an IDS resource at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/update/{resource-id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
            try {
                // Get the resource
                final var resource =
                    ((OfferedResourceServiceImpl) resourceService).getOfferedResources().get(resourceId);
                if (resource == null) {
                    // The resource could not be found, reject and inform the requester.
                    return respondResourceNotFound(resourceId);
                } else {
                    // The resource has been received, update at broker.
                    final var brokerResponse = brokerService.updateResourceAtBroker(url, resource);
                    return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
                }
            } catch (ClassCastException | NullPointerException exception) {
                return respondResourceCouldNotBeLoaded(resourceId);
            } catch (IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
        }
    }

    /**
     * Remove a resource from an ids broker
     *
     * @param url        The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Remove Resource from Broker",
        description = "Remove an IDS resource at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/remove/{resource-id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteResourceAtBroker(
        @Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
            try {
                // Get the resource
                final var resource =
                    ((OfferedResourceServiceImpl) resourceService).getOfferedResources().get(resourceId);
                if (resource == null) {
                    // The resource could not be found, reject and inform the requester.
                    return respondResourceNotFound(resourceId);
                } else {
                    // The resource has been received, remove from broker.
                    final var brokerResponse = brokerService.removeResourceFromBroker(url, resource);
                    return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
                }
            } catch (ClassCastException | NullPointerException exception) {
                return respondResourceCouldNotBeLoaded(resourceId);
            } catch (IOException exception) {
                return respondBrokerCommunicationFailed(exception);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(url);
        }
    }

    /**
     * Updates the connector object in the ids framework's config container.
     *
     * @throws ConfigurationUpdateException If the configuration could not be update.
     */
    private void updateConfigModel() throws ConfigurationUpdateException {
        BaseConnectorImpl connector = (BaseConnectorImpl) configurationContainer.getConnector();
        connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
            ._offeredResource_((ArrayList<Resource>) resourceService.getResources())
            .build()));

        ConfigurationModelImpl configurationModel =
            (ConfigurationModelImpl) configurationContainer.getConfigModel();
        configurationModel.setConnectorDescription(connector);

        configurationContainer.updateConfiguration(configurationModel);
    }
}
