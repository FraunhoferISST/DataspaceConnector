package io.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

/**
 * The interface for describing data in the backend.
 */
@Entity
@Inheritance
@Table(name = "data")
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@RequiredArgsConstructor
public class Data {

    /**
     * The primary key of the data.
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    private Long id;
}
