package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidContractException;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class IdsSerializationService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdsSerializationService.class);

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * Deserialize string to ids contract request.
     *
     * @param config The configuration model string.
     * @return The ids configuration model.
     * @throws InvalidContractException If deserialization fails.
     */
    public ConfigurationModel deserializeConfigurationModel(final String config) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(config, ConfigurationModel.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize config model. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.");
        }
    }
}
