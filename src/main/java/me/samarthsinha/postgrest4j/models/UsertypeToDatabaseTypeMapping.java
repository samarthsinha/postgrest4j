package me.samarthsinha.postgrest4j.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UsertypeToDatabaseTypeMapping {

    INTEGER("int","int"),
    TEXT("text","text"),
    DATE("date","date"),
    TIMESTAMP("timestamp","timestamp"),
    MONEY("money","money"),
    DECIMAL("decimal","decimal"),
    UUID("uuid","uuid"),
    SERIAL_NUMBER("serial","bigint");

    private String pgDataType;
    private String pgCastType;
}
