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

import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Simple wrapper for data stored in the internal database.
 */
@Entity
@Getter
@SQLDelete(sql = "UPDATE data SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Setter(AccessLevel.PACKAGE)
public class LocalData extends Data {

    /**
     * The data.
     */
    @Lob
    private byte[] value;
}
