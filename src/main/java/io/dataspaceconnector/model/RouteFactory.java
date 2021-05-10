package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class RouteFactory implements AbstractFactory<Route, RouteDesc> {

    @Override
    public Route create(RouteDesc desc) {
        return null;
    }

    @Override
    public boolean update(Route entity, RouteDesc desc) {
        return false;
    }
}
