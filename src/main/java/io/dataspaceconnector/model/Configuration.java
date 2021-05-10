package de.fraunhofer.isst.dataspaceconnector.model;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Configuration extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private String logLevel;

    private String connectorStats;

    private String deployMode;

    @OneToMany
    private List<Proxy> proxy;

    private String description;

    private String trustStore;

    private String trustStorePassword;

    private String keyStore;

    private String keyStorePassword;

    @OneToOne
    private BrokerCatalog brokerCatalog;

    @OneToOne
    private AppStoreCatalog appStoreCatalog;

    @OneToMany
    private List<App> appList;

    @OneToOne
    private ClearingHouseCatalog clearingHouseCatalog;

}
