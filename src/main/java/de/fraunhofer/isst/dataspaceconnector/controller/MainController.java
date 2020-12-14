package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints for basic connector services.
 */
@RestController
@Tag(name = "Connector: Selfservice", description = "Endpoints for connector information")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final SerializerProvider serializerProvider;
    private final ResourceService offeredResourceService, requestedResourceService;
    private final IdsUtils idsUtils;

    /**
     * Constructor for MainController.
     *
     * @throws IllegalArgumentException - if one of the parameters is null.
     */
    @Autowired
    public MainController(@NotNull SerializerProvider serializerProvider,
        @NotNull OfferedResourceServiceImpl offeredResourceService,
        @NotNull RequestedResourceServiceImpl requestedResourceService,
        @NotNull IdsUtils idsUtils)
        throws IllegalArgumentException {
        if (serializerProvider == null) {
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");
        }

        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (requestedResourceService == null) {
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");
        }

        if (idsUtils == null) {
            throw new IllegalArgumentException("The IdsUtils cannot be null.");
        }

        this.serializerProvider = serializerProvider;
        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
        this.idsUtils = idsUtils;
    }

    /**
     * Gets connector self-description without catalog.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Public Endpoint for Connector Self-description",
        description = "Get the connector's reduced self-description.")
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPublicSelfDescription() {
        Assert.notNull(idsUtils, "The idsUtils cannot be null.");
        Assert.notNull(serializerProvider, "The serializerProvider cannot be null.");

        try {
            // Modify a connector for exposing the reduced self description
            var connector = (BaseConnectorImpl) idsUtils.getConnector();
            connector.setResourceCatalog(null);
            connector.setPublicKey(null);

            return new ResponseEntity<>(serializerProvider.getSerializer().serialize(connector),
                HttpStatus.OK);
        } catch (ConnectorConfigurationException exception) {
            // No connector found
            LOGGER.warn("No connector has been configurated.", exception);
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            // Could not serialize the connector.
            LOGGER.error("Could not serialize the connector.", exception);
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets connector self-description.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Connector Self-description",
        description = "Get the connector's self-description.")
    @RequestMapping(value = {"/admin/api/self-description"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getSelfService() {
        Assert.notNull(idsUtils, "The idsUtils cannot be null.");
        Assert.notNull(serializerProvider, "The serializerProvider cannot be null.");

        try {
            // Modify a connector for exposing a resource catalog
            var connector = (BaseConnectorImpl) idsUtils.getConnector();
            connector.setResourceCatalog(Util.asList(buildResourceCatalog()));

            return new ResponseEntity<>(serializerProvider.getSerializer().serialize(connector),
                HttpStatus.OK);
        } catch (ConnectorConfigurationException exception) {
            // No connector found
            LOGGER.warn("No connector has been configurated.", exception);
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            // Could not serialize the connector.
            LOGGER.error("Could not serialize the connector.", exception);
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResourceCatalog buildResourceCatalog() throws ConstraintViolationException {
        Assert.notNull(offeredResourceService, "The offeredResourceService cannot be null.");
        Assert.notNull(requestedResourceService, "The requestedResourceService cannot be null.");

        return new ResourceCatalogBuilder()
            ._offeredResource_(new ArrayList<>(offeredResourceService.getResources()))
            ._requestedResource_(new ArrayList<>(requestedResourceService.getResources()))
            .build();
    }
}
