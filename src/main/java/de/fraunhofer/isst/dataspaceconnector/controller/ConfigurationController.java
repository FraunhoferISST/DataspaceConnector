package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * This class provides endpoints for connector configurations via a connected config manager.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api")
@Tag(name = "Connector Configuration", description = "Endpoints for connector configuration")
public class ConfigurationController {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    private ConfigurationContainer configurationContainer;
    private SerializerProvider serializerProvider;

    @Autowired
    public ConfigurationController(ConfigurationContainer configurationContainer, SerializerProvider serializerProvider) {
        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
    }

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> updateConfiguration(@RequestBody String updatedConfiguration) {
        try {
            ConfigurationModel configurationModel = serializerProvider.getSerializer().deserialize(updatedConfiguration, ConfigurationModel.class);
            configurationContainer.updateConfiguration(configurationModel);
            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (ConfigurationUpdateException | IOException e) {
            LOGGER.error("Configuration could not be updated: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getConfiguration() {
        try {
            return new ResponseEntity<>(configurationContainer.getConfigModel().toRdf(), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Configuration could not be loaded: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
