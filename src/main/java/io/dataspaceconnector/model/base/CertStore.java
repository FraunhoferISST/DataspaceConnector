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
package io.dataspaceconnector.model.base;

import io.dataspaceconnector.model.util.UriConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import java.net.URI;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * Base type for all entities.
 */
@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class CertStore extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The key store.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI location;

    /**
     * The key store password.
     */
    private String password;

    /**
     * Alias for the key store.
     */
    private String alias;
}
