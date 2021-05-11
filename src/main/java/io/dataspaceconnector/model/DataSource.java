package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

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

    private String relativePath;

    @OneToOne
    private Authentication authentication;

    private DataSourceType dataSourceType;

    @OneToMany
    private List<GenericEndpoint> genericEndpoint;
}
