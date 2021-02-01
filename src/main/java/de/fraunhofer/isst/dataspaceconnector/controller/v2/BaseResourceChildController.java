package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.CommonUniDirectionalLinkerService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BaseResourceChildController
        <T extends CommonUniDirectionalLinkerService<?>> {

    @Autowired
    private T linker;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Set<EndpointId>> getResource(
            @Valid @PathVariable final UUID id) {
        return ResponseEntity.ok(linker.get(getCurrentEndpoint(id)));
    }

    @PostMapping(value = "")
    public ResponseEntity<Void> addResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<EndpointId> resources) {
        linker.add(getCurrentEndpoint(id), new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "")
    public ResponseEntity<Void> replaceResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<EndpointId> resources) {
        linker.replace(getCurrentEndpoint(id), new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "")
    public ResponseEntity<Void> removeResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<EndpointId> resources) {
        linker.remove(getCurrentEndpoint(id), new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }

    private EndpointId getCurrentEndpoint(final UUID id) {
        var basePath = ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toString();

        final var index = basePath.lastIndexOf(id.toString());
        // -1 so that the / gets also removed
        basePath = basePath.substring(0, index - 1);

        return new EndpointId(basePath, id);
    }
}
