package io.dataspaceconnector.model;

import lombok.*;

import java.time.ZonedDateTime;

/**
 * Inner class for a time interval format.
 */
@Getter
@Setter(AccessLevel.PUBLIC)
@EqualsAndHashCode
@RequiredArgsConstructor
public class TimeInterval {

    /**
     * The start date.
     */
    private ZonedDateTime start;

    /**
     * The end date.
     */
    private ZonedDateTime end;
}
