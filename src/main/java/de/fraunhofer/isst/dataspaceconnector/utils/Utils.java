package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
     * @param obj The object to check.
     * @param message The error message transmitted when the object is null.
     * @param <T> The type of the passed object.
     * @throws IllegalArgumentException if the passed object is null.
     * @return The passed object.
     */
    public static <T> T requireNonNull(final T obj, final ErrorMessages message) {
        if (obj == null) {
            throw new IllegalArgumentException(message.toString());
        }

        return obj;
    }

    /**
     * Get a page from a list.
     * @param list The list the page  should be contructed from.
     * @param pageable The page information.
     * @param <T> The type of the list elements.
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
}
