package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates an app store.
 */
@Component
public class AppStoreFactory implements AbstractFactory<AppStore, AppStoreDesc> {

    /**
     * @param desc The description of the entity.
     * @return New app store entity.
     */
    @Override
    public AppStore create(AppStoreDesc desc) {
        return new AppStore();
    }

    /**
     * @param entity The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if entity is updated.
     */
    @Override
    public boolean update(AppStore entity, AppStoreDesc desc) {
        return false;
    }
}
