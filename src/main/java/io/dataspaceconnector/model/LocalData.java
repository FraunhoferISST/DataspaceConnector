package io.dataspaceconnector.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Simple wrapper for data stored in the internal database.
 */
@Entity
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
