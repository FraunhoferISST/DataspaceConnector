package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import io.jsonwebtoken.lang.Assert;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
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
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api")
@Tag(name = "Connector Configuration", description = "Endpoints for connector configuration")
public class ConfigurationController {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    private final ConfigurationContainer configurationContainer;
    private final SerializerProvider serializerProvider;

    /**
     * Constructor.
     *
     * @param configurationContainer The configuration.
     * @param serializerProvider     The serializer.
     * @throws IllegalArgumentException - if one of the parameters is null.
     */
    @Autowired
    public ConfigurationController(@NotNull ConfigurationContainer configurationContainer,
                                   @NotNull SerializerProvider serializerProvider) throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
    }

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateConfiguration(@RequestBody String updatedConfiguration) {
        Assert.notNull(serializerProvider, "The serializerProvider cannot be null.");
        Assert.notNull(configurationContainer, "The configurationContainer cannot be null.");

        try {
            final var serializer = serializerProvider.getSerializer();
            if (serializer == null) {
                throw new NullPointerException();
            }

            final var old_configurationModel = configurationContainer.getConfigModel();
            final var new_configurationModel =
                    serializer.deserialize(updatedConfiguration, ConfigurationModel.class);

            configurationContainer.updateConfiguration(new_configurationModel);

            LOGGER.info(String.format("Updated the configuration. Old version: \n%s\nNew version:" +
                            " %s", old_configurationModel != null ?
                            old_configurationModel.toRdf() : "No config found.",
                    new_configurationModel.toRdf()));

            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (NullPointerException exception) {
            LOGGER.error("Failed to receive the serializer.", exception);

            return new ResponseEntity<>("Failed to update configuration.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            LOGGER.error("Failed to deserialize the configuration.", exception);

            return new ResponseEntity<>("Failed to update configuration.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ConfigurationUpdateException exception) {
            LOGGER.error("Failed to update the configuration.", exception);

            return new ResponseEntity<>("Failed to update configuration.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getConfiguration() {
        final var config = configurationContainer.getConfigModel();
        if (config != null) {
            // Return the config
            return new ResponseEntity<>(config.toRdf(), HttpStatus.OK);
        } else {
            // No configuration configurated
            LOGGER.info("No configuration could be found.");
            return new ResponseEntity<>("No configuration found.", HttpStatus.NOT_FOUND);
        }
    }
}
