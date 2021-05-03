package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The base class for constructing an Infomodel object from DSC objects.
 * @param <T> The type of the DSC object.
 * @param <X> The type of the Infomodel object.
 */
@Log4j2
@NoArgsConstructor
public abstract class AbstractIdsBuilder<T extends AbstractEntity, X> {

    /**
     * The default depth the builder will follow dependencies.
     */
    public static final int DEFAULT_DEPTH = -1;

    /**
     * The max depth when searching for the setProperty Method in Infomodel objects.
     */
    private static final int MAX_DEPTH = 3;

    /**
     * Convert an DSC object to an Infomodel object. The default depth will be used to determine the
     * when to stop following dependencies.
     * @param entity The entity to be converted.
     * @return The Infomodel object.
     */
    public X create(final T entity) throws ConstraintViolationException {
        return create(entity, DEFAULT_DEPTH);
    }

    /**
     * Convert an DSC object to an Infomodel object.
     * @param entity   The entity to be converted.
     * @param maxDepth The depth determines when to stop following dependencies. Set this value to a
     *                 negative number to follow all dependencies.
     * @return The Infomodel object.
     */
    public X create(final T entity, final int maxDepth) throws ConstraintViolationException {
        return create(entity, getBaseUri(), 0, maxDepth);
    }

    private X create(final T entity, final URI baseUri, final int currentDepth,
                     final int maxDepth) throws ConstraintViolationException {
        final var resource = createInternal(entity, baseUri, currentDepth, maxDepth);
        return addAdditionals(resource, entity.getAdditional());
    }

    /**
     * This is the type specific call for converting an DSC object to an Infomodel object. The
     * additional field will be set automatically.
     * @param entity       The entity to be converted.
     * @param baseUri      The hostname of the system.
     * @param currentDepth The current distance to the original call.
     * @param maxDepth     The max depth to the original call.
     * @return The Infomodel object.
     */
    protected abstract X createInternal(T entity, URI baseUri, int currentDepth, int maxDepth)
            throws ConstraintViolationException;

    private URI getBaseUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
    }

    /**
     * Use this function to construct the absolute path to this entity.
     * @param entity  The entity.
     * @param baseUri The hostname.
     * @param <X>     The entity type.
     * @return The absolute path to this entity.
     */
    protected <X extends AbstractEntity> URI getAbsoluteSelfLink(final X entity,
                                                                 final URI baseUri) {
        var uri = SelfLinkHelper.getSelfLink(entity);

        if (!uri.isAbsolute()) {
            uri = URI.create(baseUri.toString() + uri);
        }

        return uri;
    }

    private static boolean shouldGenerate(final int currentDepth, final int maxDepth) {
        return currentDepth <= maxDepth || maxDepth < 0;
    }

    // NOTE: The type of ArrayList is used because the Infomodel object expects ArrayList for some
    // unknown reason. By changing the return type from List to ArrayList it is more convenient
    // to use since no typecast is required.

    /**
     * Batch call of create. Use this call for building an object's dependencies. This function
     * increments the currentDepth.
     * @param builder      The builder applied to all objects.
     * @param entityList   The entities that need to be converted.
     * @param baseUri      The hostname of the system.
     * @param currentDepth The current distance to the original call.
     * @param maxDepth     The distance to the original call.
     * @param <V>          The type of the DSC Entity.
     * @param <W>          The type of the Infomodel Entity.
     * @return The converted Infomodel objects. Null if the distance is to far to the original call.
     */
    protected <V extends AbstractEntity, W> Optional<ArrayList<W>> create(
            final AbstractIdsBuilder<V, W> builder, final List<V> entityList, final URI baseUri,
            final int currentDepth, final int maxDepth) throws ConstraintViolationException {
        final int nextDepth = currentDepth + 1;

        return !shouldGenerate(nextDepth, maxDepth) ? Optional.empty()
                : Optional.of(new ArrayList<>(Utils.toStream(entityList)
                                                   .map(r -> builder
                                                           .create(r, baseUri, nextDepth, maxDepth))
                                                   .filter(Objects::nonNull)
                                                   .collect(Collectors.toList())));
    }

    private <X> X addAdditionals(final X idsResource, final Map<String, String> additional) {
        // NOTE: The Infomodel lib has setProperty on all classes, but the method is implemented
        // individually...
        try {
            final var setPropertyMethod = findAdditionalMethod(idsResource);
            for (final var entry : additional.entrySet()) {
                setPropertyMethod.invoke(idsResource, entry.getKey(), entry.getValue());
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to set additional fields. [exception=({})]", e.getMessage(), e);
            }
        }

        return idsResource;
    }

    private <X> Method findAdditionalMethod(final X idsResource) throws NoSuchMethodException {
        // NOTE: The Infomodel lib has setProperty on all classes, but some of them are implemented
        // higher up the inheritance chain.
        // If the setProperty method has a different signature null is returned.
        var tClass = idsResource.getClass();
        for (int i = 0; i < MAX_DEPTH; i++) {
            try {
                return tClass.getMethod("setProperty", String.class, Object.class);
            } catch (NoSuchMethodException ignore) {
                // Intentionally empty
            }
            if (i < MAX_DEPTH - 1) {
                tClass = tClass.getSuperclass();
            }
        }

        throw new NoSuchMethodException();
    }
}
