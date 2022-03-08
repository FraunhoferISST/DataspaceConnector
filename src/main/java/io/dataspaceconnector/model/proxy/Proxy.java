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
package io.dataspaceconnector.model.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.base.RemoteService;
import io.dataspaceconnector.model.util.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * Entity for managing proxies.
 */
@javax.persistence.Entity
@Table(name = "proxy")
@SQLDelete(sql = "UPDATE proxy SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class Proxy extends Entity implements RemoteService {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The proxy uri.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI location;

    /**
     * List of no proxy uris.
     */
    @ElementCollection
    private List<String> exclusions;

    /**
     * The authentication information for the proxy.
     */
    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JsonInclude
    @ToString.Exclude
    private BasicAuth authentication;
}
