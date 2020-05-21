package me.samarthsinha.postgrest4j.repositories;

import me.samarthsinha.postgrest4j.models.MasterConfigurations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterConfigurationRepo extends JpaRepository<MasterConfigurations, Integer> {

    MasterConfigurations findFirstByTenantIdAndMasterName(String tenantId, String masterName);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE master_configurations SET _published = true, _active=true WHERE _id = ?1",nativeQuery = true)
    int activateMasterConfig(Integer id);

}
