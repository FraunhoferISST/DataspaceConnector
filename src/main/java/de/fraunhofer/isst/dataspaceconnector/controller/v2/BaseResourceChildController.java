package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.BaseUniDirectionalLinkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class
BaseResourceChildController<T extends BaseUniDirectionalLinkerService> {

    @Autowired
    private T linker;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Set<UUID>> getResource(
            @Valid @PathVariable final UUID id) {
        return ResponseEntity.ok(linker.get(id));
    }

    @PostMapping(value = "")
    public ResponseEntity<Void> addResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<UUID> resources) {
        linker.add(id, new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "")
    public ResponseEntity<Void> replaceResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<UUID> resources) {
        linker.replace(id, new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "")
    public ResponseEntity<Void> removeResources(
            @Valid @PathVariable final UUID id,
            @Valid @RequestBody final List<UUID> resources) {
        linker.remove(id, new HashSet<>(resources));
        return ResponseEntity.ok().build();
    }
}
