package de.fraunhofer.isst.dataspaceconnector.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class contains general purpose functions.
 */
@UtilityClass
public class Utils {

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


    public static Page<?> toPage( final List<?> list, final Pageable pageable) {
        Utils.requireNonNull(list, ErrorMessages.LIST_NULL);
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);

        if (pageable.equals(Pageable.unpaged())) {
            return new PageImpl<>(list, pageable, list.size());
        }

        final var start = (int)pageable.getOffset();
        final var end = Math.min(start + pageable.getPageSize(), list.size());

        if(start > pageable.getPageSize()) {
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }

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
