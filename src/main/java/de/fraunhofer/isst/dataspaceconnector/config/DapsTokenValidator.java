package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class DapsTokenValidator implements PermissionEvaluator {

    private DapsTokenProvider tokenProvider;

    @Autowired
    public void setTokenProvider(final DapsTokenProvider provider) {
        this.tokenProvider = provider;
    }

    @Override
    public boolean hasPermission(
            final Authentication auth, final Object targetDomainObject, final Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege();
    }

    @Override
    public boolean hasPermission(final Authentication auth, final Serializable targetId,
            final String targetType, final Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege();
    }

    private boolean hasPrivilege() {
        if (tokenProvider == null) {
            return false;
        } else {
            return tokenProvider.getDAT() != null;
        }
    }
}
