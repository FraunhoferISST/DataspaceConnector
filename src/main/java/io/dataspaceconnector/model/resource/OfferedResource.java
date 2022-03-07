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
package io.dataspaceconnector.model.resource;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Describes resources offered by this connector.
 */
@SQLDelete(sql = "UPDATE resource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@EqualsAndHashCode(callSuper = true)
public class OfferedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The catalogs in which this resource is used.
     */
    @ManyToMany(mappedBy = "offeredResources")
    private List<Catalog> catalogs;

    /**
     * The list of brokers where the resource is registered.
     */
    @ManyToMany(mappedBy = "offeredResources")
    private List<Broker> brokers;

    /**
     * Default constructor.
     */
    protected OfferedResource() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalogs(final List<Catalog> catalogList) {
        this.catalogs = catalogList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBrokers(final List<Broker> brokerList) {
        this.brokers = brokerList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Broker> getBrokers() {
        return brokers;
    }
}
