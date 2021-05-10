package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

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

    private URI proxyURI;

    @ElementCollection
    private List<URI> noProxyURI;

    @OneToOne
    private ProxyAuthentication proxyAuthentication;

}
