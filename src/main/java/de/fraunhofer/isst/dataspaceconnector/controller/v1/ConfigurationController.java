package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.services.IdsSerializationService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configContainer;

    /**
     * Service for deserializing ids objects.
     */
    private final @NonNull IdsSerializationService idsService;

    /**
     * Update the connector's current configuration.
     *
     * @param configuration The new configuration.
     * @return Ok or error response.
     */
    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<String> updateConfiguration(@RequestBody final String configuration) {
        try {
            // Deserialize input.
            final var newConfig = idsService.deserializeConfigurationModel(configuration);

            // Update configuration of connector.
            configContainer.updateConfiguration(newConfig);
            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (ConfigurationUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (IllegalArgumentException exception) {
            return ControllerUtils.responseDeserializationError(exception);
        }
    }

    /**
     * Return the connector's current configuration.
     *
     * @return The configuration object or an error.
     */
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @ResponseBody
    public ResponseEntity<String> getConfiguration() {
        final var config = configContainer.getConfigModel();
        if (config == null) {
            return respondConfigurationNotFound();
        } else {
            return new ResponseEntity<>(config.toRdf(), HttpStatus.OK);
        }
    }
}
