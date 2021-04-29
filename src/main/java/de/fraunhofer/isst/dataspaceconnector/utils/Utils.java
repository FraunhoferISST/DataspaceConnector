package de.fraunhofer.isst.dataspaceconnector.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This utility class contains general purpose functions.
 */
public final class Utils {

    /**
     * Default constructor.
     */
    private Utils() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Check if an object is not null.
     *
     * @param obj     The object to check.
     * @param message The error message transmitted when the object is null.
     * @param <T>     The type of the passed object.
     * @return The passed object.
     * @throws IllegalArgumentException if the passed object is null.
     */
    public static <T> T requireNonNull(final T obj, final ErrorMessages message) {
        if (obj == null) {
            throw new IllegalArgumentException(message.toString());
        }

        return obj;
    }

    /**
     * Convert a collection that may be null safely to a stream.
     * If the collection is null an empty stream will be produced.
     *
     * @param collection The collection. May be null.
     * @param <T>        The type of the elements in the collection.
     * @return The stream over the elements of the collection.
     */
    public static <T> Stream<T> toStream(final Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }

    /**
     * Get a page from a list.
     *
     * @param list     The list the page  should be constructed from.
     * @param pageable The page information.
     * @param <T>      The type of the list elements.
     * @return The new page.
     */
    public static <T> Page<T> toPage(final List<T> list, final Pageable pageable) {
        Utils.requireNonNull(list, ErrorMessages.LIST_NULL);
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);

        if (pageable.equals(Pageable.unpaged())) {
            // All elements should be returned.
            return new PageImpl<>(list, pageable, list.size());
        }

        final var start = (int) pageable.getOffset();

        if (start > pageable.getPageSize()) {
            // There are no more list elements.
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }

        final var end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * Check if a parameter is empty.
     *
     * @param <T>   Type of input parameter.
     * @param param The parameter.
     * @return True if it is empty, false if not.
     */
    public static <T> boolean isEmptyOrNull(final T param) {
        if (param == null) {
            return true;
        } else {
            return param.toString().equals("");
        }
    }

    /**
     * DO NOT USE.
     *
     * @param sort Sorting instructions within user input.
     * @return Java object of input value.
     */
    public static Sort toSort(final String sort) {
        // TODO Implement with Regex
        if (sort == null) {
            return Sort.unsorted();
        }

        final var comma = sort.indexOf(",");
        if (comma == -1) {
            return Sort.by(sort);
        }

        try {
            final var dirString = sort.substring(comma + 1, sort.length()).toUpperCase();
            final var dir = Sort.Direction.valueOf(dirString);
            final var property = sort.substring(0, comma);
            return Sort.by(dir, property);
        } catch (Exception e) {
            return Sort.unsorted();
        }
    }

    /**
     * Creates a page request based on page, size, and sort inputs.
     *
     * @param page The page index.
     * @param size The page size.
     * @param sort The sorting of page.
     * @return The page request.
     */
    public static PageRequest toPageRequest(final Integer page, final Integer size,
                                            final String sort) {
        final int pageIndex = (page != null && page > 0) ? page : DEFAULT_FIRST_PAGE;
        final int sizeValue = (size != null && size > 0) ? Math.min(size, MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        return PageRequest.of(pageIndex, sizeValue, Utils.toSort(sort));
    }

    /**
     * Default page size.
     */
    public static final int DEFAULT_PAGE_SIZE = 30;

    /**
     * Max page size.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Default first page.
     */
    public static final int DEFAULT_FIRST_PAGE = 0;
}
