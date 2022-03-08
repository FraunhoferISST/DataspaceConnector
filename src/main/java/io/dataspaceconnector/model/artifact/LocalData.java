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
package io.dataspaceconnector.model.artifact;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Simple wrapper for data stored in the internal database.
 */
@Entity
@SQLDelete(sql = "UPDATE data SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Setter(AccessLevel.PUBLIC)
public class LocalData extends Data {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The data.
     */
    @Lob
    private byte[] value;

    /**
     * Get the data.
     *
     * @return The data.
     */
    public byte[] getValue() {
        return value == null ? null : value.clone();
    }
}
