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
package io.dataspaceconnector.service.message;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.UUIDFormatException;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.service.resource.relation.AppStoreAppLinker;
import io.dataspaceconnector.service.resource.type.AppStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

/**
 * Service for handling app store logic during ids communication.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AppStoreCommunication {

    /**
     * Service for linking apps and app stores.
     */
    private final @NotNull AppStoreAppLinker linker;

    /**
     * Service for the broker.
     */
    private final @NotNull AppStoreService appStoreService;

    /**
     * Check if input is an appStore id or an url.
     *
     * @param input The input uri.
     * @return The location of the appStore object or the original uri (url).
     */
    public URI checkInput(final URI input) {
        try {
            final var appStore = appStoreService.get(UUIDUtils.uuidFromUri(input));
            return appStore.getLocation();
        } catch (UUIDFormatException exception) {
            // Input uri is not an appStore id. Proceed.
        } catch (ResourceNotFoundException exception) {
            // No appStore found for this id. Proceed.
        }
        return input;
    }

    /**
     * Link app to app store entity.
     *
     * @param input The uri of the recipient/ id of the app store.
     * @param appId The id of the stored app entity.
     */
    public void addAppToAppStore(final URI input, final UUID appId) {
        try {
            final var appStore = appStoreService.get(UUIDUtils.uuidFromUri(input));
            linker.add(appStore.getId(), Set.of(appId));
        } catch (UUIDFormatException | ResourceNotFoundException exception) {
            // No appStore found for this id. App could not be linked. Proceed.
            if (log.isWarnEnabled()) {
                log.warn("Failed to link app to app store. [recipient=({}), appId=({})]", input,
                        appId);
            }
        }
    }
}
