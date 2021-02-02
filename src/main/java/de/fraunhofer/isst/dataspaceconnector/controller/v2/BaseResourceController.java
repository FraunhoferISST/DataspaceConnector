package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.BaseView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.FrontFacingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BaseResourceController<T extends BaseResource,
        D extends BaseDescription<T>, V extends BaseView<T>, S extends FrontFacingService<T, D, V>> {
    @Autowired
    private S service;

    @PostMapping(value = "")
    public ResponseEntity<Void> create(@RequestBody final D desc) {
        final var endpointId = service.create(ServletUriComponentsBuilder
                .fromCurrentRequest().build().toString(), desc);

        final var headers = new HttpHeaders();
        headers.setLocation(endpointId.toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Set<EndpointId>> get() {
        // TODO change format to "ref": "link"
        final var resources = service.getAll();
        return ResponseEntity.ok(resources);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<V> get(@Valid @PathVariable final UUID id) {
        final var resource = service.get(getCurrentEndpoint(id));
        return ResponseEntity.ok(resource);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@Valid @PathVariable final UUID id,
                                       @RequestBody final D desc) {
        final var endpointId = getCurrentEndpoint(id);
        final var newEndpoint = service.update(getCurrentEndpoint(id),
                desc);

        if (newEndpoint.equals(endpointId)) {
            // The resource was not moved
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } else {
            // The resource has been moved
            var headers = new HttpHeaders();
            headers.setLocation(newEndpoint.toUri());

            return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable final UUID id) {
        service.delete(getCurrentEndpoint(id));
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    protected S getService() {
        return service;
    }

    public static EndpointId getCurrentEndpoint(final UUID id) {
        var basePath = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toString();

        final var index = basePath.lastIndexOf(id.toString());
        // -1 so that the / gets also removed
        basePath = basePath.substring(0, index - 1);

        return new EndpointId(basePath, id);
    }
}
