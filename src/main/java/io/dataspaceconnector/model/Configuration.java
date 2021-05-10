package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "configuration")
@SQLDelete(sql = "UPDATE configuration SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Configuration extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    @Enumerated(EnumType.STRING)
    private ConnectorStatus connectorStatus;

    @Enumerated(EnumType.STRING)
    private ConnectorDeployMode deployMode;

    @OneToMany
    private List<Proxy> proxy;

    private String trustStore;

    private String trustStorePassword;

    private String keyStore;

    private String keyStorePassword;
}
