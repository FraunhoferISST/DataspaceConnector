package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.*;
import io.dataspaceconnector.services.resources.OfferedResourceService;
import io.dataspaceconnector.services.resources.OwningRelationService;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public final class EntityLinkerSerivce {

    @Service
    @NoArgsConstructor
    public static class AppStoreAppLinker
            extends OwningRelationService<AppStore, App, AppStoreService, AppService> {

        @Override
        protected final List<App> getInternal(final AppStore owner) {
            return owner.getAppList();
        }
    }

    @Service
    @NoArgsConstructor
    public static class BrokerOfferedResourcesLinker
            extends OwningRelationService<Broker, OfferedResource, BrokerService, OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Broker owner) {
            return owner.getOfferedResources();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ConfigurationProxyLinker
            extends OwningRelationService<Configuration, Proxy, ConfigurationService, ProxyService> {

        @Override
        protected final List<Proxy> getInternal(final Configuration owner) {
            return owner.getProxy();
        }
    }

    @Service
    @NoArgsConstructor
    public static class DataSourceAuthenticationLinker
            extends OwningRelationService<DataSource, Authentication, DataSourceService, AuthenticationService> {

        protected Authentication getAuthentication(final DataSource owner) {
            return Utils.requireNonNull(owner.getAuthentication(), ErrorMessages.AUTH_NULL);
        }

        @Override
        protected List<Authentication> getInternal(final DataSource owner) {
            return null;
        }
    }

    @Service
    @NoArgsConstructor
    public static class DataSourceGenericEndpointsLinker
            extends OwningRelationService<DataSource, GenericEndpoint, DataSourceService, GenericEndpointService> {

        @Override
        protected List<GenericEndpoint> getInternal(final DataSource owner) {
            return owner.getGenericEndpoint();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ProxyAuthenticationLinker
            extends OwningRelationService<Proxy, Authentication, ProxyService, AuthenticationService> {

        protected Authentication getAuthentication(final Proxy owner) {
            return Utils.requireNonNull(owner.getAuthentication(), ErrorMessages.AUTH_NULL);
        }

        @Override
        protected List<Authentication> getInternal(final Proxy owner) {
            return null;
        }
    }


}
