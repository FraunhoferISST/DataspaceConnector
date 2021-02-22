package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondConfigurationNotFound;

/**
 * This class provides endpoints for connector configurations via a connected config manager.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Connector")
@RequiredArgsConstructor
public class ConfigurationController {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * The provider for ids serialization.
     */
    private final @NonNull SerializerProvider serializerProvider;

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<String> updateConfiguration(@RequestBody String updatedConfiguration) {
        try {
            final var serializer = serializerProvider.getSerializer();
            if (serializer == null) {
                throw new NullPointerException("No configuration serializer has been set.");
            }

            final var new_configurationModel =
                serializer.deserialize(updatedConfiguration, ConfigurationModel.class);

            configurationContainer.updateConfiguration(new_configurationModel);
            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (NullPointerException exception) {
            LOGGER.warn("Failed to receive the serializer. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            LOGGER.warn("Failed to deserialize the configuration. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ConfigurationUpdateException exception) {
            LOGGER.warn("Failed to update the configuration. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Hidden
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @ResponseBody
    public ResponseEntity<String> getConfiguration() {
        final var config = configurationContainer.getConfigModel();
        if (config != null) {
            // Return the config
            return new ResponseEntity<>(config.toRdf(), HttpStatus.OK);
        } else {
            return respondConfigurationNotFound();
        }
    }
}
