package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.App;
import io.dataspaceconnector.model.AppDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for apps.
 */
@Service
@NoArgsConstructor
public class AppService extends BaseEntityService<App, AppDesc> {
}
