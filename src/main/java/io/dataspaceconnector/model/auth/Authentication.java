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
package io.dataspaceconnector.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dataspaceconnector.common.net.HttpAuthentication;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Base element for all authentication types.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance
@SQLDelete(sql = "UPDATE authentication SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "authentication")
public abstract class Authentication implements HttpAuthentication, Serializable {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The primary key.
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @SuppressWarnings("PMD.ShortVariable")
    private Long id;

    /**
     * Whether this entity is considered deleted.
     */
    @Column(name = "deleted")
    private boolean deleted;
}
