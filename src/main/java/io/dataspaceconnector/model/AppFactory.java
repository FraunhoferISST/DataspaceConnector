package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates an app.
 */
@Component
public class AppFactory implements AbstractFactory<App, AppDesc> {

    /**
     * Creates an app.
     *
     * @param desc The description of the entity.
     * @return new app entity.
     */
    @Override
    public App create(final AppDesc desc) {
        return new App();
    }

    /**
     * Updates an app with the description.
     *
     * @param entity The entity to be updated.
     * @param desc   The description of the new entity.
     * @return true, if app is updated
     */
    @Override
    public boolean update(final App entity, final AppDesc desc) {
        return false;
    }
}
