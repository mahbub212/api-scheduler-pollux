package id.co.msiglife.pollux.schduler.repository.CancelPolis;

import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcTm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PtcTmRepository extends JpaRepository<MstPtcTm, String> {
}

