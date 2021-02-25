package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.BaseUniDirectionalLinkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Offers REST-Api Endpoints for modifying relations between resources.
 *
 * @param <T> The service type for handling the relations logic.
 */
public class BaseResourceChildController
        <T extends BaseUniDirectionalLinkerService<?, ?, ?, ?>> {

    /**
     * The linker between two resources.
     **/
    @Autowired
    private T linker;

    /**
     * Default constructor.
     */
    protected BaseResourceChildController() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Get all resources of the same type linked to the passed resource.
     * Endpoint for GET requests.
     *
     * @param ownerId The id of the owning resource.
     * @return The children of the resource.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Set<UUID>> getResource(
            @Valid @PathVariable(name = "id") final UUID ownerId) {
        return ResponseEntity.ok(linker.get(ownerId));
    }

    /**
     * Add resources as children to a resource. Endpoint for POST requests.
     *
     * @param ownerId   The owning resource.
     * @param resources The children to be added.
     * @return Response with code 200 (Ok) and the new children's list.
     */
    @PostMapping
    public ResponseEntity<Set<UUID>> addResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<EndpointId> resources) {
        // TODO
        // linker.add(ownerId, new HashSet<>(resources));
        linker.add(ownerId, null);
        // Send back the list of children after modification.
        // See https://tools.ietf.org/html/rfc7231#section-4.3.3 and
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/200
        return this.getResource(ownerId);
    }

    /**
     * Replace the children of a resource. Endpoint for PUT requests.
     *
     * @param ownerId   The id of the resource which children should be
     *                  replaced.
     * @param resources The resources that should be added as children.
     * @return Response with code 204 (No_Content).
     */
    @PutMapping
    public ResponseEntity<Void> replaceResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<EndpointId> resources) {
        // TODO
        // linker.replace(ownerId, new HashSet<>(resources));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Remove a list of children of a resource. Endpoint for DELETE requests.
     *
     * @param ownerId   The id of the resource which children should be removed.
     * @param resources The list of children to be removed.
     * @return Response with code 204 (No_Content).
     */
    @DeleteMapping
    public ResponseEntity<Void> removeResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<EndpointId> resources) {
        // TODO
        // linker.remove(ownerId, new HashSet<>(resources));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
