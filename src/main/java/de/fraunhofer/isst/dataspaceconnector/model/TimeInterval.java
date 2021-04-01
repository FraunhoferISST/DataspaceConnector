package de.fraunhofer.isst.dataspaceconnector.model;

import java.time.ZonedDateTime;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
