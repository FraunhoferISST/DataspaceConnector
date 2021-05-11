package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationFactory implements AbstractFactory<Authentication, AuthenticationDesc> {


    @Override
    public Authentication create(AuthenticationDesc desc) {
        return null;
    }

    @Override
    public boolean update(Authentication entity, AuthenticationDesc desc) {
        return false;
    }
}
