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
package io.dataspaceconnector.model.representation;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.named.NamedEntity;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.model.subscription.Subscription;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * A representation describes how data is presented.
 */
@javax.persistence.Entity
@Table(name = "representation")
@SQLDelete(sql = "UPDATE representation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Representation extends NamedEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The representation id on provider side.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI remoteId;

    /**
     * The media type expressed by this representation.
     */
    private String mediaType;

    /**
     * The language used by this representation.
     */
    private String language;

    /**
     * "Standard followed at representation level, i.e. it governs
     * the serialization of an abstract content like RDF/XML."
     */
    private String standard;

    /**
     * The artifacts associated with this representation.
     */
    @ManyToMany
    private List<Artifact> artifacts;

    /**
     * The resources associated with this representation.
     */
    @ManyToMany(mappedBy = "representations")
    private List<Resource> resources;

    /**
     * List of subscriptions listening to updates for this representation.
     */
    @OneToMany
    private List<Subscription> subscriptions;
}
