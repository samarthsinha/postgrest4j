package me.samarthsinha.postgrest4j.controller;

import me.samarthsinha.postgrest4j.models.ConfigFilter;
import me.samarthsinha.postgrest4j.models.InsertDto;
import me.samarthsinha.postgrest4j.models.MasterConfigurations;
import me.samarthsinha.postgrest4j.models.QueryFilter;
import me.samarthsinha.postgrest4j.services.PostgresDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TempController {

    @Autowired
    PostgresDbService postgresDbService;

    @PostMapping("/publish-master")
    public Object getSchema(@RequestBody ConfigFilter configFilter) throws Exception {
        return postgresDbService.createUnderlyingTableForMaster(configFilter);
    }

    @PostMapping("/define-master")
    public Object registerMasterSchema(@RequestBody MasterConfigurations masterConfigurations) throws Exception {
        return postgresDbService.createMasterDataDefinition(masterConfigurations);
    }

    @PutMapping("/put-data")
    public Object insertData(@RequestBody InsertDto insertDto) throws Exception {
        return postgresDbService.insertData(insertDto);
    }

    @PostMapping("/filter")
    public Object filterData(@RequestBody QueryFilter filter) throws Exception {
        return postgresDbService.filterData(filter);
    }
}
