package id.co.msiglife.pollux.schduler.repository;

import id.co.msiglife.pollux.schduler.entity.ListWFStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListWFStatusRepository extends JpaRepository<ListWFStatus, String> {
    ListWFStatus findListWFStatusByTypeIdAndStatusId(Integer typeId, Integer statusId);
}
