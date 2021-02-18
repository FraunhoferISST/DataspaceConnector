package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ConfigurationModelImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.IdsResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondBrokerCommunicationFailed;
import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondRejectUnauthorized;
import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondResourceCouldNotBeLoaded;
import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondResourceNotFound;
import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondUpdateError;

/**
 * This class provides endpoints for the communication with an IDS broker instance.
 */
@RestController
@RequestMapping("/api/ids/broker")
@Tag(name = "IDS Messages",
    description = "Endpoints for invoke sending IDS messages")
public class BrokerController {

    private final DapsTokenProvider tokenProvider;
    private final IDSBrokerService brokerService;
    private final BFFResourceService<OfferedResource, ?, OfferedResourceView> offeredResourceService;
    private final ConfigurationContainer configurationContainer;

    @Autowired
    private IdsResourceService idsResourceService;

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
    public BrokerController(final DapsTokenProvider tokenProvider,
                            final ConfigurationContainer configurationContainer,
                            final BFFResourceService<OfferedResource, ?, OfferedResourceView> offeredResourceService,
                            final IDSBrokerService brokerService)
        throws IllegalArgumentException {

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (brokerService == null)
            throw new IllegalArgumentException("The IDSBrokerService cannot be null.");

        this.tokenProvider = tokenProvider;
        this.configurationContainer = configurationContainer;
        this.brokerService = brokerService;
        this.offeredResourceService = offeredResourceService;
    }

    /**
     * Notify an IDS broker of the availability of this connector or one of its resources.
     *
     * @param url The broker address.
     * @param resourceId The resource uuid.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Register or update connector/resource",
        description = "Register or update the connector or a resource at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAtBroker(
            @Parameter(description = "The url of the broker.",
            required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
            @RequestParam("broker") String url,
            @Parameter(description = "The resource id.")
            @RequestParam(value = "resourceId", required = false) UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
            if (resourceId == null) {
                return updateConnectorAtBroker(url);
            } else {
                return updateResourceAtBroker(url, resourceId);
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
    @Operation(summary = "Unregister connector/resource", description = "Unregister connector or " +
            "resource from an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unregisterAtBroker(
        @Parameter(description = "The url of the broker.",
            required = true, example = "https://broker.ids.isst.fraunhofer.de/infrastructure")
        @RequestParam("broker") String url,
        @Parameter(description = "The resource id.")
        @RequestParam(value = "resourceId", required = false) UUID resourceId) {
        // Make sure the request is authorized.
        if (tokenProvider.getDAT() != null) {
            if (resourceId == null) {
                return unregisterConnectorAtBroker(url);
            } else {
                return unregisterResourceAtBroker(url, resourceId);
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
     * Updates the connector object in the ids framework's config container.
     *
     * @throws ConfigurationUpdateException If the configuration could not be update.
     */
    private void updateConfigModel() throws ConfigurationUpdateException {
        BaseConnectorImpl connector = (BaseConnectorImpl) configurationContainer.getConnector();
        connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
            ._offeredResource_((ArrayList<? extends Resource>) idsResourceService.getAllOfferedResources())
            .build()));

        ConfigurationModelImpl configurationModel =
            (ConfigurationModelImpl) configurationContainer.getConfigModel();
        configurationModel.setConnectorDescription(connector);

        configurationContainer.updateConfiguration(configurationModel);
    }

    /**
     * Updates or registers the connector at an IDS broker.
     *
     * @param url The recipient's address.
     * @return The http response.
     */
    private ResponseEntity<String> updateConnectorAtBroker(String url) {
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
    }

    /**
     * Updates or registers a resource at an IDS broker.
     *
     * @param url The recipient's address.
     * @param resourceId The resource id.
     * @return The http response.
     */
    private ResponseEntity<String> updateResourceAtBroker(String url, UUID resourceId) {
        try {
            // Get the resource
            // TODO: the filter seems wrong
            final var resource = idsResourceService.getAllOfferedResources().stream().filter(x -> x.getId().toString().contains(resourceId.toString())).findAny();
            if (resource.isEmpty()) {
                // The resource could not be found, reject and inform the requester.
                return respondResourceNotFound(resourceId);
            } else {
                // The resource has been received, update at broker.
                final var brokerResponse = brokerService.updateResourceAtBroker(url, resource.get());
                return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
            }
        } catch (ClassCastException | NullPointerException exception) {
            return respondResourceCouldNotBeLoaded(resourceId);
        } catch (IOException exception) {
            return respondBrokerCommunicationFailed(exception);
        }
    }

    /**
     * Removes the connector from an IDS broker.
     *
     * @param url The recipient's address.
     * @return The http response.
     */
    private ResponseEntity<String> unregisterConnectorAtBroker(String url) {
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
    }

    /**
     * Removes a resource from an IDS broker.
     *
     * @param url The recipient's address.
     * @param resourceId The resource id.
     * @return The http response.
     */
    private ResponseEntity<String> unregisterResourceAtBroker(String url, UUID resourceId) {
        try {
            // Get the resource
            // TODO: the filter seems wrong
            final var resource = idsResourceService.getAllOfferedResources().stream().filter(x -> x.getId().toString().contains(resourceId.toString())).findAny();
            if (resource.isEmpty()) {
                // The resource could not be found, reject and inform the requester.
                return respondResourceNotFound(resourceId);
            } else {
                // The resource has been received, remove from broker.
                final var brokerResponse = brokerService.removeResourceFromBroker(url, resource.get());
                return new ResponseEntity<>(brokerResponse.body().string(), HttpStatus.OK);
            }
        } catch (ClassCastException | NullPointerException exception) {
            return respondResourceCouldNotBeLoaded(resourceId);
        } catch (IOException exception) {
            return respondBrokerCommunicationFailed(exception);
        }
    }
}
