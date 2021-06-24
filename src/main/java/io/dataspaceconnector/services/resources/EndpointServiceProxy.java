package io.dataspaceconnector.services.resources;

import java.util.UUID;

import io.dataspaceconnector.model.endpoints.Endpoint;
import io.dataspaceconnector.model.endpoints.EndpointDesc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EndpointServiceProxy implements EntityService<Endpoint, EndpointDesc> {
    @Override
    public Endpoint create(EndpointDesc desc) {
        return null;
    }

    @Override
    public Endpoint update(UUID entityId, EndpointDesc desc) {
        return null;
    }

    @Override
    public Endpoint get(UUID entityId) {
        return null;
    }

    @Override
    public Page<Endpoint> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean doesExist(UUID entityId) {
        return false;
    }

    @Override
    public void delete(UUID entityId) {

    }
}
