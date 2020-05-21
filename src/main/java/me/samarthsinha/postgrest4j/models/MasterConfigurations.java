package me.samarthsinha.postgrest4j.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "public",name = "master_configurations")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class MasterConfigurations extends AuditModel implements Serializable {

    private static final long serialVersionUID = 1929239292919183L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private Integer id;
    @Column(name = "_tenant_id")
    private String tenantId;
    @Column(name = "_org_id")
    private String orgId;
    @Column(name = "master_name")
    private String masterName;
    @JsonIgnore
    @Column(name = "_master_table_name")
    private String tableName;
    @Column(name = "columns_details")
    @Type(type = "jsonb")
    private List<ColumnDefinition> columnsDetails;
    @Column(name = "_published")
    private boolean published;
    @Column(name = "_active")
    private boolean active;
}
