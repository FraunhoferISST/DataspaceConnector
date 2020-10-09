package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.BrokerService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * This class provides endpoints for the communication with an IDS broker instance.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/broker")
@Tag(name = "Connector: IDS Broker Communication", description = "Endpoints for invoking broker communication")
public class BrokerController { // TODO add update resources
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(BrokerController.class);

    private TokenProvider tokenProvider;
    private BrokerService brokerService;

    @Autowired
    /**
     * <p>Constructor for BrokerController.</p>
     *
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param configProducer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param keyStoreManager a {@link de.fraunhofer.isst.ids.framework.util.KeyStoreManager} object.
     */
    public BrokerController(TokenProvider tokenProvider, ConfigurationContainer configurationContainer) {
        this.tokenProvider = tokenProvider;
        try {
            this.brokerService = new BrokerService(configurationContainer, new ClientProvider(configurationContainer),
                    tokenProvider);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("(Framework) Broker Service Error: " + e.getMessage());
        }
    }

    /**
     * Sends a ConnectorAvailableMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Register Connector", description = "Register or update connector at an IDS broker.")
    @RequestMapping(value = {"/register", "/update"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> updateAtBroker(@Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure") @RequestParam("broker") String url) {
        if (tokenProvider.getTokenJWS() != null) {
            try {
                return new ResponseEntity<>(brokerService.updateAtBroker(url).body().string(), HttpStatus.OK);
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
     * Sends a ConnectorUnavailableMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Unregister Connector", description = "Unregister connector at an IDS broker.")
    @RequestMapping(value = "/unregister", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> unregisterAtBroker(@Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure") @RequestParam("broker") String url) {
        if (tokenProvider.getTokenJWS() != null) {
            try {
                return new ResponseEntity<>(brokerService.unregisterAtBroker(url).body().string(), HttpStatus.OK);
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
     * Sends a QueryMessage to an IDS broker.
     *
     * @param url The broker address.
     * @return The broker response message or an error.
     */
    @Operation(summary = "Broker Query Request", description = "Send a query request to an IDS broker.")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> queryBroker(@Parameter(description = "The url of the broker.", required = true,
            example = "https://broker.ids.isst.fraunhofer.de/infrastructure") @RequestParam("broker") String url) {
        if (tokenProvider.getTokenJWS() != null) {
            String query = "SELECT ?subject ?predicate ?object\n" +
                    "FROM <urn:x-arq:UnionGraph>\n" +
                    "WHERE {\n" +
                    "  ?subject ?predicate ?object\n" +
                    "}";

            try {
                return new ResponseEntity<>(brokerService.queryBroker(url, query, null, null, null).body().string(), HttpStatus.OK);
            } catch (IOException e) {
                LOGGER.error("Broker communication failed: " + e.getMessage());
                return new ResponseEntity<>("Broker communication failed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.error("No DAT token found");
            return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
        }
    }
}
