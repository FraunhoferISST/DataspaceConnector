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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.dataspaceconnector.model.utils.UriConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.net.URI;
import java.util.List;

/**
 * Entity for managing proxies.
 */
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class Proxy {

    /**
     * The primary key of the authentication.
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    @SuppressWarnings("PMD.ShortVariable")
    private Long id;

    /**
     * The proxy uri.
     */
    @Convert(converter = UriConverter.class)
    private URI proxyURI;

    /**
     * List of no proxy uris.
     */
    @ElementCollection
    private List<URI> noProxyURI;

    /**
     * The authentication information for the proxy.
     */
    @OneToOne(cascade = { CascadeType.ALL })
    @JsonInclude
    @ToString.Exclude
    private Authentication authentication;
}
