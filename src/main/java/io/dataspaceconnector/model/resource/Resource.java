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

import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.named.NamedEntity;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.util.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * A resource describes offered or requested data.
 */
@javax.persistence.Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE resource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "resource")
@RequiredArgsConstructor
public class Resource extends NamedEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The keywords of the resource.
     */
    @ElementCollection
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI publisher;

    /**
     * The owner of the resource.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI sovereign;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The license of the resource.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI license;

    /**
     * The endpoint of the resource.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI endpointDocumentation;

    /**
     * The version of the resource.
     */
    @Version
    private long version;

    /**
     * The payment method.
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentModality;

    /**
     * Links to sample resources.
     */
    @ElementCollection
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private List<URI> samples;

    /**
     * The representation available for the resource.
     */
    @ManyToMany
    private List<Representation> representations;

    /**
     * The contracts available for the resource.
     */
    @ManyToMany
    private List<Contract> contracts;

    /**
     * Set the catalogs used by this resource.
     *
     * @param catalogList The catalog list.
     */
    public void setCatalogs(final List<Catalog> catalogList) {
        /*
            NOTE: Offered and Requested Resource override this function.
         */
        throw new NotImplemented();
    }

    /**
     * Get the list of catalogs used by this resource.
     *
     * @return The list of catalogs used by this resource.
     */
    public List<Catalog> getCatalogs() {
        /*
            NOTE: Offered and Requested Resource override this function
            so that exception should never be returned. Throw exception
            here so that a missing override crashes really load.
         */
        throw new NotImplemented();
    }

    /**
     * @param brokers The broker list.
     */
    public void setBrokers(final List<Broker> brokers) {
        /*
            NOTE: Offered Resource override this function.
         */
        throw new NotImplemented();
    }

    /**
     * @return The list of brokers used by this resource.
     */
    public List<Broker> getBrokers() {
        /*
            NOTE: Offered Resource override this function
         */
        throw new NotImplemented();
    }

    /**
     * List of subscriptions listening to updates for this resource.
     */
    @OneToMany
    private List<Subscription> subscriptions;
}
