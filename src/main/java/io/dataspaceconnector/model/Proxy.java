package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.ListUriConverter;
import io.dataspaceconnector.model.utils.UriConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

/**
 * Entity for managing proxies.
 */
@Entity
@Table(name = "proxy")
@SQLDelete(sql = "UPDATE proxy SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Proxy extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The proxy uri.
     */
    @Convert(converter = UriConverter.class)
    private URI proxyURI;

    /**
     * List of no proxy uris.
     */
    @Convert(converter = ListUriConverter.class)
    private List<URI> noProxyURI;

    /**
     * The authentication for the proxy.
     */
    @OneToOne
    private Authentication authentication;

}
