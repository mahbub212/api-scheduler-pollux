package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.ListWFConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListWFConfigRepository extends JpaRepository<ListWFConfig, String> {
    @Query("SELECT lwc FROM ListWFConfig lwc WHERE lwc.typeId = :typeId AND lwc.stateIdFrom = :stateIdFrom AND lwc.stateIdTo = :stateIdTo AND lwc.isActive = 1")
    ListWFConfig findListWFConfigByTypeIdAndStateIdFromAndStateIdTo(Integer typeId, Integer stateIdFrom, Integer stateIdTo);
}
