package me.samarthsinha.postgrest4j.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryFilter {
    private ConfigFilter configFilter;
    private Map<String, List<Object>> dataFilter;
}
