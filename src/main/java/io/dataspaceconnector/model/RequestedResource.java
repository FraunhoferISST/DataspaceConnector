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

import java.net.URI;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Describes resource requested by this connector.
 */
@Entity
@SQLDelete(sql = "UPDATE resource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public final class RequestedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The resource id on provider side.
     */
    private URI remoteId;

    /**
     * Default constructor.
     */
    protected RequestedResource() {
        super();
    }

    /**
     * List of backends subscribed to resource updates.
     */
    @ElementCollection
    @JsonProperty("subscribers")
    private List<URI> subscribers;

    /**
     * The catalogs in which this resource is used.
     */
    @ManyToMany(mappedBy = "requestedResources")
    private List<Catalog> catalogs;

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
     * Sets the list of subscribers for this resource.
     *
     * @param subscriberList the list of subscribers.
     */
    public void setSubscribers(final List<URI> subscriberList) {
        this.subscribers = subscriberList;
    }
}
