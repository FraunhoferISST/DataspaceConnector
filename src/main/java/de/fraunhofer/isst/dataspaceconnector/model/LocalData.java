package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Simple wrapper for data stored in the internal database.
 */
@Entity
@Table
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Setter(AccessLevel.PACKAGE)
public class LocalData extends Data {

    /**
     * The data.
     */
    @Lob
    private byte[] value;
}
