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
package io.dataspaceconnector.controller.configurations;

import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.Authentication;
import io.dataspaceconnector.model.ClearingHouse;
import io.dataspaceconnector.model.ClearingHouseDesc;
import io.dataspaceconnector.model.Configuration;
import io.dataspaceconnector.model.ConfigurationDesc;
import io.dataspaceconnector.model.Connector;
import io.dataspaceconnector.model.ConnectorDesc;
import io.dataspaceconnector.model.DataSource;
import io.dataspaceconnector.model.DataSourceDesc;
import io.dataspaceconnector.model.IdentityProvider;
import io.dataspaceconnector.model.IdentityProviderDesc;
import io.dataspaceconnector.model.Proxy;
import io.dataspaceconnector.services.configuration.ClearingHouseService;
import io.dataspaceconnector.services.configuration.ConfigurationService;
import io.dataspaceconnector.services.configuration.ConnectorsService;
import io.dataspaceconnector.services.configuration.DataSourceService;
import io.dataspaceconnector.services.configuration.IdentityProviderService;
import io.dataspaceconnector.view.ClearingHouseView;
import io.dataspaceconnector.view.ConfigurationView;
import io.dataspaceconnector.view.ConnectorView;
import io.dataspaceconnector.view.DataSourceView;
import io.dataspaceconnector.view.IdentityProviderView;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

/**
 * Controller for the Configuration Manager.
 */
public final class ConfigmanagerControllers {

    /**
     * Offers the endpoints for managing clearing houses.
     */
    @RestController
    @RequestMapping("/api/clearinghouses")
    @Tag(name = "Clearing House", description = "Endpoints for CRUD operations on clearing houses")
    public static class ClearingHouseController
            extends BaseResourceController<ClearingHouse, ClearingHouseDesc,
            ClearingHouseView, ClearingHouseService> {
    }

    /**
     * Offers the endpoints for managing configurations.
     */
    @RestController
    @RequestMapping("/api/configurations")
    @RequiredArgsConstructor
    @Tag(name = "Configuration", description = "Endpoints for CRUD operations on configurations")
    public static class ConfigurationController
            extends BaseResourceController<Configuration, ConfigurationDesc,
            ConfigurationView, ConfigurationService> {

        /**
         * The configuration service.
         */
        private final @NonNull ConfigurationService configurationService;

        /**
         *
         * @param configurationId The id of the configuration
         * @param proxy The new proxy
         * @return HttpStatus Ok.
         * @throws IOException Exception occurs, if proxy can not be set at configuration.
         */
        @PutMapping(value = "{id}/proxy")
        public ResponseEntity<Void> putProxy(
                @Valid @PathVariable(name = "id") final UUID configurationId,
                @RequestBody final Proxy proxy) throws IOException {
            configurationService.setConfigurationProxyInformation(configurationId, proxy);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Offers the endpoints for managing connectors.
     */
    @RestController
    @RequestMapping("/api/connectors")
    @Tag(name = "Connector Configuration",
            description = "Endpoints for CRUD operations on connectors")
    public static class ConnectorController
            extends BaseResourceController<Connector, ConnectorDesc, ConnectorView,
            ConnectorsService> {
    }

    /**
     * Offers the endpoints for managing data sources.
     */
    @RestController
    @RequestMapping("/api/datasources")
    @RequiredArgsConstructor
    @Tag(name = "Data Source", description = "Endpoints for CRUD operations on data sources")
    public static class DataSourceController
            extends BaseResourceController<DataSource, DataSourceDesc, DataSourceView,
            DataSourceService> {

        /**
         * The service managing data sources.
         */
        private final @NonNull DataSourceService dataSourceService;

        /**
         * Replace the authentication of a data source.
         * @param dataSourceId   The data source whose authentication should be replaced.
         * @param authentication The new authentication.
         * @return Http Status ok.
         */
        @PutMapping(value = "{id}/authentication")
        public ResponseEntity<Void> putAuthentication(
                @Valid @PathVariable(name = "id") final UUID dataSourceId,
                @RequestBody final Authentication authentication) throws IOException {
            dataSourceService.setDataSourceAuthentication(dataSourceId, authentication);
            return ResponseEntity.ok().build();
        }

        /**
         * Deletes the authentication from the data source.
         * @param dataSourceId The data source whose authentication should be deleted.
         * @return Http Status ok.
         */
        @DeleteMapping(value = "{id}/authentication")
        public ResponseEntity<Void> deleteAuthentication(
                @Valid @PathVariable(name = "id") final UUID dataSourceId) throws IOException {
            dataSourceService.deleteDataSourceAuthentication(dataSourceId);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Offers the endpoints for managing identity provider endpoints.
     */
    @RestController
    @RequestMapping("/api/identityproviders")
    @Tag(name = "Identity Provider", description = "Endpoints for CRUD operations on"
            + " identity providers")
    public static class IdentityProviderController
            extends BaseResourceController<IdentityProvider, IdentityProviderDesc,
            IdentityProviderView, IdentityProviderService> {
    }
}
