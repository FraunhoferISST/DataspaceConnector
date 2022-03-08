/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.contract;

import io.dataspaceconnector.model.base.RemoteObject;
import io.dataspaceconnector.model.named.NamedEntity;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.util.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * A contract documents access and usage behaviours.
 */
@javax.persistence.Entity
@Table(name = "contract")
@SQLDelete(sql = "UPDATE contract SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Contract extends NamedEntity implements RemoteObject {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The contract id on provider side.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI remoteId;

    /**
     * The consumer signing the contract.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI consumer;

    /**
     * The provider signing the contract.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI provider;

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
