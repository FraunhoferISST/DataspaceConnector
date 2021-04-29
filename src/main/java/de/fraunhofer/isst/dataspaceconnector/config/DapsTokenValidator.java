package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * This class provides DAT validation.
 */
@Component
@AllArgsConstructor
public final class DapsTokenValidator implements PermissionEvaluator {

    /**
     * Service for providing current DAT.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

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
