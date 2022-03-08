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
package io.dataspaceconnector.model.agreement;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.base.RemoteObject;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * A contract agreement is an agreement between two parties on access
 * and usage behaviours.
 */
@javax.persistence.Entity
@Table(name = "agreement")
@SQLDelete(sql = "UPDATE agreement SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Agreement extends Entity implements RemoteObject {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The agreement id on provider side.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI remoteId;

    /**
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * Signal that this agreement expired and may not be used anymore.
     */
    private boolean archived;

    /**
     * The definition of the contract.
     **/
    @Lob
    private String value;

    /**
     * The artifacts this agreement refers to.
     */
    @ManyToMany
    private List<Artifact> artifacts;
}
