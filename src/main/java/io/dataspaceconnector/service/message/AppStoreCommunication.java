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
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.service.resource.relation.AppAppStoreLinker;
import io.dataspaceconnector.service.resource.relation.AppStoreAppLinker;
import io.dataspaceconnector.service.resource.type.AppStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;
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
     * Service for linking apps and app stores.
     */
    private final @NotNull AppAppStoreLinker appAppStoreLinker;

    /**
     * Check if input is an appStore id or an url.
     *
     * @param input The input uri.
     * @return The found appStore or empty.
     */
    public Optional<AppStore> checkInput(final URI input) {
        try {
            final var appStore = appStoreService.getAppStoreByLocation(input);
            return appStore == null ? Optional.empty() : Optional.of(appStore);
        } catch (UUIDFormatException exception) {
            // Input uri is not an appStore id. Proceed.
        } catch (ResourceNotFoundException exception) {
            // No appStore found for this id. Proceed.
        }
        return Optional.empty();
    }

    /**
     * Link app to app store entity.
     *
     * @param appStore The app store from which the app is downloaded.
     * @param appId    The id of the stored app entity.
     */
    public void addAppToAppStore(final Optional<AppStore> appStore, final UUID appId) {
        try {
            if (appStore.isPresent()) {
                linker.add(appStore.get().getId(), Set.of(appId));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Input was not an app store. Could not link app to app store. "
                            + "[appId=({})]", appId);
                }
            }
        } catch (UUIDFormatException | ResourceNotFoundException exception) {
            // No appStore found for this id. App could not be linked. Proceed.
            if (log.isWarnEnabled()) {
                log.warn("Failed to link app to app store. [appId=({})]", appId);
            }
        }
    }

    /**
     * Link app store to app.
     *
     * @param appId The id of the app.
     * @param appStoreId The id of the app store.
     */
    public void addAppStoreToApp(final UUID appId, final UUID appStoreId) {
        appAppStoreLinker.add(appId, Set.of(appStoreId));
    }
}
