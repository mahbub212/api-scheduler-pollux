package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.MasterWfRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MasterWfRequestRepository extends JpaRepository<MasterWfRequest, String> {

    @Modifying
    @Query("UPDATE MasterWfRequest m SET m.lswfConfigAction = :configAction, m.lswfConfigId = :tmpConfigId WHERE m.lswfConfigId = :configId AND m.lswfTypeId = :typeId")
    Integer updateMasterWfRequestConfigActionByLswfConfigIdAndLswfTypeId(
            @Param("configId") Integer configId,
            @Param("typeId") Integer typeId,
            @Param("configAction") String configAction,
            @Param("tmpConfigId") Integer tmpConfigId
    );
}
