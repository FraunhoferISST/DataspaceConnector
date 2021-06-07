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

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.net.URI;
import java.time.ZonedDateTime;

/**
 * Entity class for the identity provider.
 */
@Entity
@Inheritance
@Table(name = "identityprovider")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE identityprovider SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@RequiredArgsConstructor
public class IdentityProvider extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the identity provider.
     */
    @Convert(converter = UriConverter.class)
    private URI accessUrl;

    /**
     * The title of the identity provider.
     */
    private String title;

    /**
     * The registration status.
     */
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;
}
