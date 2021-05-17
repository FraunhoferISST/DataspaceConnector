package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Configuration;
import io.dataspaceconnector.model.ConfigurationDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for the configuration.
 */
@Service
@NoArgsConstructor
public class ConfigurationService extends BaseEntityService<Configuration, ConfigurationDesc> {
}
