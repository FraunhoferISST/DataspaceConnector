package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 * Entity which holds information about the data sources.
 */
@Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE datasource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "datasource")
@RequiredArgsConstructor
public class DataSource extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The relative path of the data source.
     */
    private String relativePath;

    /**
     * The authentication for the data source.
     */
    @OneToOne
    private Authentication authentication;

    /**
     * The type of the data source.
     */
    @Enumerated(EnumType.STRING)
    private DataSourceType dataSourceType;

    /**
     * The list of generic endpoints.
     */
    @OneToMany
    private List<GenericEndpoint> genericEndpoint;
}
