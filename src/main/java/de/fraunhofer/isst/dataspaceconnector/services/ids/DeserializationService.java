package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResponseMessage;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageDeserializationException;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service class for ids object deserialization.
 */
@Service
@RequiredArgsConstructor
public class DeserializationService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeserializationService.class);

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider provider;

    /**
     * Deserialize string to ids configuration model.
     *
     * @param config The configuration model string.
     * @return The ids configuration model.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ConfigurationModel deserializeConfigurationModel(final String config)
            throws IllegalArgumentException {
        try {
            return provider.getSerializer().deserialize(config, ConfigurationModel.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize config model. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.");
        }
    }

    /**
     * Deserialize string to ids infrastructure component.
     *
     * @param component The infrastructure component string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public InfrastructureComponent deserializeInfrastructureComponent(final String component)
            throws IllegalArgumentException {
        try {
            return provider.getSerializer().deserialize(component, InfrastructureComponent.class);
        } catch (IOException e) {
            LOGGER.debug("Could not deserialize component. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.");
        }
    }

    /**
     * Deserialize string to ids resource.
     *
     * @param resource The resource string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Resource deserializeResource(final String resource) throws IllegalArgumentException {
        try {
            return provider.getSerializer().deserialize(resource, Resource.class);
        } catch (IOException e) {
            LOGGER.debug("Could not deserialize resource. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.");
        }
    }

    /**
     * Returns the ids header of a http multipart response.
     *
     * @param response A ids response message.
     * @return The response message.
     * @throws MessageDeserializationException If deserialization fails.
     */
    public ResponseMessage deserializeResponseMessage(final String response)
            throws MessageDeserializationException {
        try {
            return provider.getSerializer().deserialize(response, ResponseMessage.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize response message. [exception=({})]", e.getMessage());
            throw new MessageDeserializationException("Could not deserialize response message.");
        }
    }
}
