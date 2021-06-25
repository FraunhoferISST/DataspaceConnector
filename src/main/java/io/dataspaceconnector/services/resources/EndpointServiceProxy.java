package io.dataspaceconnector.services.resources;

import java.util.UUID;

import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.model.endpoints.AppEndpointDesc;
import io.dataspaceconnector.model.endpoints.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoints.Endpoint;
import io.dataspaceconnector.model.endpoints.EndpointDesc;
import io.dataspaceconnector.repositories.EndpointRepository;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.ConnectorEndpointService;
import io.dataspaceconnector.services.configuration.GenericEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EndpointServiceProxy implements EntityService<Endpoint, EndpointDesc> {

    @Autowired
    GenericEndpointService generic;

    @Autowired
    AppEndpointService apps;

    @Autowired
    ConnectorEndpointService connector;

    @Autowired
    private EndpointRepository repository;

    private <X extends Endpoint, Y extends EndpointDesc> EntityService<X, Y> getService(final Class<?> clazz) {
        if (AppEndpointDesc.class.equals(clazz)) {
            return (EntityService<X, Y>) apps;
        } else if (ConnectorEndpointDesc.class.equals(clazz)) {
            return (EntityService<X, Y>) connector;
        }

        return (EntityService<X, Y>) generic;
    }

    @Override
    public Endpoint create(final EndpointDesc desc) {
        return getService(desc.getClass()).create(desc);
    }

    @Override
    public Endpoint update(final UUID entityId, final EndpointDesc desc) {
        return getService(desc.getClass()).update(entityId, desc);
    }

    @Override
    public Endpoint get(final UUID entityId) {
        try {
            return apps.get(entityId);
        } catch(ResourceNotFoundException ignored) { }

        try {
            return connector.get(entityId);
        } catch(ResourceNotFoundException ignored) { }

        return generic.get(entityId);
    }

    @Override
    public Page<Endpoint> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean doesExist(final UUID entityId) {
        try {
            return apps.doesExist(entityId);
        } catch(ResourceNotFoundException ignored) { }

        try {
            return connector.doesExist(entityId);
        } catch(ResourceNotFoundException ignored) { }

        return generic.doesExist(entityId);
    }

    @Override
    public void delete(final UUID entityId) {
        apps.get(entityId);
        connector.get(entityId);
        generic.get(entityId);
    }
}
