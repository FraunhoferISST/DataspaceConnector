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
package io.dataspaceconnector.controller.configuration;

import io.dataspaceconnector.controller.resource.BaseResourceController;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.appstore.AppStoreDesc;
import io.dataspaceconnector.service.configuration.AppStoreService;
import io.dataspaceconnector.view.appstore.AppStoreView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the app store entities.
 */
public class AppStoreControllers {
    /**
     * Offers the endpoints for managing app stores.
     */
    @RestController
    @RequestMapping("/api/appstores")
    @Tag(name = ResourceName.APPSTORES, description = ResourceDescription.APPSTORE)
    public static class AppStoresController extends BaseResourceController<AppStore, AppStoreDesc,
            AppStoreView, AppStoreService> {
    }
}
