package me.samarthsinha.postgrest4j.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ColumnDefinition implements Serializable {

    private static final long serialVersionUID = 728382818128282L;
    private String columnName;
    private String tableColumnName;
    private String dataType;
    private boolean searchable;
    private boolean unique;
}
