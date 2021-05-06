package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Proxy extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private URI proxyURI;

    @ElementCollection
    private List<URI> noProxyURI;

    @OneToOne
    private ProxyAuthentication proxyAuthentication;

}
