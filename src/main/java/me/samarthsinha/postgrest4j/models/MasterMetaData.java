package me.samarthsinha.postgrest4j.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MasterMetaData {
    private String tenantId;
    private String masterName;
}
