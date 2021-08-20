package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.service.resource.base.OwningRelationService;
import io.dataspaceconnector.service.resource.type.AppService;
import io.dataspaceconnector.service.resource.type.AppStoreService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the relation between app store and related apps.
 */
@Service
@NoArgsConstructor
public class AppStoreAppLinker extends OwningRelationService<AppStore, App, AppStoreService,
        AppService> {

    @Override
    protected final List<App> getInternal(final AppStore owner) {
        return owner.getApps();
    }
}
