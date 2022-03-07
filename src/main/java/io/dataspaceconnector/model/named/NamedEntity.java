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
package io.dataspaceconnector.model.named;

import io.dataspaceconnector.model.base.Entity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import static io.dataspaceconnector.model.config.DatabaseConstants.DESCRIPTION_COLUMN_LENGTH;

/**
 * The entity class which holds additional information like title, description.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class NamedEntity extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;
    /**
     * The title of the entity.
     */
    private String title;
    /**
     * The description of the entity.
     */
    @Column(length = DESCRIPTION_COLUMN_LENGTH)
    private String description;
}
