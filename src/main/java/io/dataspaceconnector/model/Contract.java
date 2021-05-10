/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@Table(name = "contract")
@SQLDelete(sql = "UPDATE contract SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
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
