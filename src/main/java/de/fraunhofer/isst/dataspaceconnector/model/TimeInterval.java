package de.fraunhofer.isst.dataspaceconnector.model;

import java.time.ZonedDateTime;

import lombok.Data;

/**
 * Inner class for a time interval format.
 */
@Data
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
