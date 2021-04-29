package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A contract documents access and usage behaviours.
 */
@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Contract extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The contract id on provider side.
     */
    private URI remoteId;

    /**
     * The consumer signing the contract.
     */
    private URI consumer;

    /**
     * The provider signing the contract.
     */
    private URI provider;

    /**
     * The title of the contract.
     */
    private String title;

    /**
     * Contract start time and date.
     */
    @Column(name = "contract_start")
    private ZonedDateTime start;

    /**
     * Contract end time and date.
     */
    @Column(name = "contract_end")
    private ZonedDateTime end;

    /**
     * The rules used by this contract.
     **/
    @ManyToMany
    private List<ContractRule> rules;

    /**
     * The representations in which this contract is used.
     */
    @ManyToMany(mappedBy = "contracts")
    private List<Resource> resources;
}
