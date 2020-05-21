package me.samarthsinha.postgrest4j.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InsertDto {
    private ConfigFilter configFilter;
    private List<Map<String,Object>> data;
}
