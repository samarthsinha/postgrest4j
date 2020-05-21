package me.samarthsinha.postgrest4j.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFilter {
    private String tenantId;
    private String orgId;
    private String masterName;
}
