package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.*;
import io.dataspaceconnector.services.resources.OfferedResourceService;
import io.dataspaceconnector.services.resources.OwningRelationService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public final class EntityLinkerService {

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

        @Override
        protected List<Authentication> getInternal(final DataSource owner) {
            return List.of(owner.getAuthentication());
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

        @Override
        protected List<Authentication> getInternal(final Proxy owner) {
            return List.of(owner.getAuthentication());
        }
    }

    @Service
    @NoArgsConstructor
    public static class RouteSubrouteLinker
            extends OwningRelationService<Route, Route, RouteService, RouteService> {

        @Override
        protected List<Route> getInternal(final Route owner) {
            return owner.getSubRoutes();
        }
    }

    /**
     * ToDO: Discuss how it could be solved with the @Service Annotation
     */

//    @Service
    @NoArgsConstructor
    public static class RouteStartEndpointLinker
            extends OwningRelationService<Route, Endpoint, RouteService,
            EndpointService<Endpoint, EndpointDesc<Endpoint>>> {

        @Override
        protected List<Endpoint> getInternal(final Route owner) {
            var endpoints = new ArrayList<Endpoint>();
            if (EndpointType.GENERIC_ENDPOINT.equals(owner.getStartEndpoint().getEndpointType())
                    || EndpointType.IDS_ENDPOINT.equals(owner.getStartEndpoint().getEndpointType())) {
                endpoints = (ArrayList<Endpoint>) List.of(owner.getStartEndpoint());
            }
            return endpoints;
        }
    }

    /**
     * ToDO: Discuss how it could be solved with the @Service Annotation
     */

//  @Service
    @NoArgsConstructor
    public static class RouteEndEndpointLinker
            extends OwningRelationService<Route, Endpoint, RouteService,
            EndpointService<Endpoint, EndpointDesc<Endpoint>>> {

        @Override
        protected List<Endpoint> getInternal(final Route owner) {
            var endpoints = new ArrayList<Endpoint>();
            if (EndpointType.GENERIC_ENDPOINT.equals(owner.getStartEndpoint().getEndpointType())
                    || EndpointType.IDS_ENDPOINT.equals(owner.getStartEndpoint().getEndpointType())) {
                endpoints = (ArrayList<Endpoint>) List.of(owner.getEndEndpoint());
            }
            return endpoints;
        }
    }

    @Service
    @NoArgsConstructor
    public static class RouteOfferedResourceLinker
            extends OwningRelationService<Route, OfferedResource, RouteService, OfferedResourceService> {

        @Override
        protected List<OfferedResource> getInternal(final Route owner) {
            return owner.getOfferedResources();
        }
    }

}
