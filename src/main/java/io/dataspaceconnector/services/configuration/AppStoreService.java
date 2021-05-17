package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.AppStore;
import io.dataspaceconnector.model.AppStoreDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for app store.
 */
@Service
@NoArgsConstructor
public class AppStoreService extends BaseEntityService<AppStore, AppStoreDesc> {
}
