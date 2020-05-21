package me.samarthsinha.postgrest4j.services;

import me.samarthsinha.postgrest4j.models.ConfigFilter;
import me.samarthsinha.postgrest4j.models.InsertDto;
import me.samarthsinha.postgrest4j.models.MasterConfigurations;
import me.samarthsinha.postgrest4j.models.QueryFilter;

public interface DbServices {
    Object createUnderlyingTableForMaster(ConfigFilter configFilter) throws Exception;
    Object createMasterDataDefinition(MasterConfigurations masterConfigurations) throws Exception;
    Object insertData(InsertDto insertDto) throws Exception;
    Object filterData(QueryFilter queryFilter) throws Exception;
}
