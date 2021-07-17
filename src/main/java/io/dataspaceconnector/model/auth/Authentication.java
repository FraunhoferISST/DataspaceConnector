package io.dataspaceconnector.model.auth;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dataspaceconnector.service.HttpService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Base element for all authentication types.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Authentication implements HttpService.Authentication {
    /** The primary key. */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    @SuppressWarnings("PMD.ShortVariable")
    private Long id;
}
