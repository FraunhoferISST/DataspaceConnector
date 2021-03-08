package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;

import java.util.Date;

/**
 * Inner class for a time interval format.
 */
@Data
public class TimeInterval {

    /**
     * The start date.
     */
    private Date start;

    /**
     * The end date.
     */
    private Date end;
}
