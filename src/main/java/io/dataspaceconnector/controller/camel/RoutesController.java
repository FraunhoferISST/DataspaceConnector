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
package io.dataspaceconnector.controller.camel;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for adding and removing routes at runtime.
 */
@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesController.class);

    /**
     * The Camel context.
     */
    private final DefaultCamelContext camelContext;

    /**
     * Unmarshaller for reading route definitions from XML.
     */
    private final Unmarshaller unmarshaller;

    /**
     * Constructor for the RoutesController.
     *
     * @param context the CamelContext.
     * @param xmlUnmarshaller the Unmarshaller.
     */
    @Autowired
    public RoutesController(final CamelContext context, final Unmarshaller xmlUnmarshaller) {
        this.camelContext = (DefaultCamelContext) context;
        this.unmarshaller = xmlUnmarshaller;
    }

    /**
     * Adds one or more routes from an XML file to the Camel context.
     *
     * @param file the XML file.
     * @return a response entity with code 200 or 500, if an error occurs.
     */
    @PostMapping
    @Operation(summary = "Add a route to the Camel context.")
    @Tag(name = "Camel", description = "Endpoints for dynamically managing Camel routes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Route file is missing or invalid."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @Hidden
    public ResponseEntity<String> addRoutes(@RequestParam("file") final MultipartFile file) {
        try {
            if (file == null) {
                throw new IllegalArgumentException("File must not be null");
            }

            final var inputStream = file.getInputStream();
            final var routes = (RoutesDefinition) unmarshaller.unmarshal(inputStream);
            camelContext.addRouteDefinitions(routes.getRoutes());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Added {} routes to the Camel Context.", routes.getRoutes().size());
            }

            return new ResponseEntity<>("Successfully added " + routes.getRoutes().size()
                    + " routes to Camel Context.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Could not read XML file because file was null.");
            }
            return new ResponseEntity<>("File must not be null.", HttpStatus.BAD_REQUEST);
        } catch (JAXBException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Could not read route(s) from XML file. [exception=({})]",
                        e.getMessage(), e);
            }
            return new ResponseEntity<>("Could not read route(s) from XML file: "
                    + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Could not add route(s) to Camel Context. [exception=({})]",
                        e.getMessage(), e);
            }
            return new ResponseEntity<>("Could not add route(s) to Camel Context: "
                    + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a route from the Camel context by its ID.
     *
     * @param routeId the route ID.
     * @return a response entity with code 200 or 500, if an error occurs.
     */
    @DeleteMapping("/{routeId}")
    @Operation(summary = "Delete a route from the Camel context.")
    @Tag(name = "Camel", description = "Endpoints for dynamically managing Camel routes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @Hidden
    public ResponseEntity<String> removeRoute(@PathVariable("routeId") final String routeId) {
        try {
            camelContext.stopRoute(routeId);

            if (!camelContext.removeRoute(routeId)) {
                throw new Exception("Could not remove route because route was not stopped.");
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Stopped route with ID {} and removed it from the Camel Context",
                        routeId);
            }

            return new ResponseEntity<>("Successfully stopped and removed route with ID "
                    + routeId, HttpStatus.OK);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Could not remove route with ID {} from Camel context. "
                                + "[exception=({})]", routeId, e.getMessage(), e);
            }
            return new ResponseEntity<>("Could not stop or remove route: "
                    + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

