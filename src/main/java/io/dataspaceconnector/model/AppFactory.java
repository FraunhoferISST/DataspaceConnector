package de.fraunhofer.isst.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class AppFactory implements AbstractFactory<App, AppDesc> {

    @Override
    public App create(AppDesc desc) {
        return null;
    }

    @Override
    public boolean update(App entity, AppDesc desc) {
        return false;
    }
}
