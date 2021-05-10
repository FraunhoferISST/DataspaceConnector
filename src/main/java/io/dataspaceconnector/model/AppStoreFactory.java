package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class AppStoreFactory implements AbstractFactory<AppStore, AppStoreDesc> {

    @Override
    public AppStore create(AppStoreDesc desc) {
        return null;
    }

    @Override
    public boolean update(AppStore entity, AppStoreDesc desc) {
        return false;
    }
}
