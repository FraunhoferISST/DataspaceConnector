package io.dataspaceconnector.service.resource.ids.builder;

import java.util.List;
import java.util.Optional;

import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.ModelClass;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.util.ApiReferenceHelper;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Abstract class for IDS builders that construct IDS objects from DSC routes.
 *
 * @param <X> The type of IDS object created from the DSC route.
 */
@RequiredArgsConstructor
public abstract class IdsRouteBuilder<X extends ModelClass> extends AbstractIdsBuilder<Route, X> {

    /**
     * The builder for IDS endpoints.
     */
    private final @NonNull IdsEndpointBuilder endpointBuilder;

    /**
     * The builder for ids artifacts.
     */
    private final @NonNull IdsArtifactBuilder artifactBuilder;

    /**
     * Helper class for managing API endpoint references.
     */
    private final @NonNull ApiReferenceHelper apiReferenceHelper;

    protected abstract X createInternal(Route entity, int currentDepth, int maxDepth)
            throws ConstraintViolationException;

    /**
     * Builds the start for a route. The start may be null if the route is used to dispatch data
     * from the DSC to backends.
     *
     * @param route        The route.
     * @param currentDepth Current depth of the build process.
     * @param maxDepth     Maximum depth of the build process.
     * @return An optional containing the built endpoint, if any.
     */
    protected Optional<List<Endpoint>> buildRouteStart(final Route route, final int currentDepth,
                                                       final int maxDepth) {
        Optional<List<Endpoint>> start = Optional.empty();
        if (route.getStart() != null) {
            start = create(endpointBuilder, Util.asList(route.getStart()), currentDepth, maxDepth);
        }
        return start;
    }

    /**
     * Builds the end for a route. If the route end is null, but the route references an artifact
     * as output, an IDS {@link de.fraunhofer.iais.eis.ConnectorEndpoint} is built from the
     * artifact.
     *
     * @param route        The route.
     * @param currentDepth Current depth of the build process.
     * @param maxDepth     Maximum depth of the build process.
     * @return An optional containing the built endpoint, if any.
     */
    protected Optional<List<Endpoint>> buildRouteEnd(final Route route, final int currentDepth,
                                                     final int maxDepth) {
        Optional<List<Endpoint>> end = Optional.empty();
        if (route.getEnd() != null) {
            end = create(endpointBuilder, Util.asList(route.getEnd()), currentDepth, maxDepth);
        } else {
            final var output = route.getOutput();
            if (output != null) {
                final var artifacts =
                        create(artifactBuilder, Util.asList(output), currentDepth, maxDepth);
                final var accessUrl = apiReferenceHelper.getDataUri(output);

                final var builder = new ConnectorEndpointBuilder(accessUrl)
                        ._endpointInformation_(new TypedLiteral("Connector endpoint for artifact "
                                + output.getId(), "en"))
                        ._accessURL_(accessUrl);
                artifacts.ifPresent(a -> builder._endpointArtifact_(artifacts.get().get(0)));

                end = Optional.of(Util.asList(builder.build()));
            }
        }
        return end;
    }

}
