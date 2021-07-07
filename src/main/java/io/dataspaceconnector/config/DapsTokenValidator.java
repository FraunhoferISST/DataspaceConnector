/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.config;

import io.dataspaceconnector.service.ids.ConnectorService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * This class provides DAT validation.
 */
@Log4j2
@Component
@AllArgsConstructor
public final class DapsTokenValidator implements PermissionEvaluator {

    /**
     * Service for providing connector information.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    public boolean hasPermission(
            final Authentication auth, final Object targetDomainObject, final Object permission) {
        if (auth == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege();
    }

    @Override
    public boolean hasPermission(final Authentication auth, final Serializable targetId,
                                 final String targetType, final Object permission) {
        if (auth == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege();
    }

    private boolean hasPrivilege() {
        return connectorService.getCurrentDat() != null;
    }
}
