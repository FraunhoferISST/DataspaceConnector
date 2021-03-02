package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
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
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * The provider for ids serialization.
     */
    private final @NonNull SerializerProvider serializerProvider;

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
            final var serializer = serializerProvider.getSerializer();
            if (serializer == null) {
                throw new NullPointerException("No configuration serializer has been set.");
            }

            final var newConfigModel =
                serializer.deserialize(configuration, ConfigurationModel.class);

            configurationContainer.updateConfiguration(newConfigModel);
            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (NullPointerException exception) {
            return ControllerUtils.responseFailedToLoadSerializer(exception);
        } catch (ConfigurationUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (IOException exception) {
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
        final var config = configurationContainer.getConfigModel();
        if (config != null) {
            // Return the config.
            return new ResponseEntity<>(config.toRdf(), HttpStatus.OK);
        } else {
            return respondConfigurationNotFound();
        }
    }
}
