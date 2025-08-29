package id.co.msiglife.pollux.schduler.service.JournalService;

import id.co.msiglife.pollux.schduler.entity.Journal.MstPtcTm;
import id.co.msiglife.pollux.schduler.entity.polis.MasterProduction;
import id.co.msiglife.pollux.schduler.exception.NotBalancedException;
import id.co.msiglife.pollux.schduler.projection.CancelJmProjection;
import id.co.msiglife.pollux.schduler.repository.polis.PtcJmRepository;
import id.co.msiglife.pollux.schduler.repository.polis.PtcTmRepository;
import id.co.msiglife.pollux.schduler.service.CancelPolisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class JournalService {

    @Autowired
    private PtcJmRepository ptcJmRepository;

    @Autowired
    private PtcTmRepository ptcTmRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insertMstTmJm(List<CancelJmProjection> rows, MasterProduction masterProduction, Date dateNow, String noJm, PtcJmRepository ptcJmRepository){

        insertMstTm(masterProduction, dateNow, noJm);

        rows.forEach(r -> ptcJmRepository.insertMstPtcJm(
                r.getNO_JM(),
                r.getNO_JURNAL(),
                r.getKETERANGAN(),
                r.getMJM_JUMLAH(),
                r.getMJM_CD(),
                r.getMJM_PROJECT(),
                r.getMJM_BUDGET(),
                r.getTYPE_TRX(),
                r.getKEY_JURNAL(),
                r.getLKU_ID(),
                r.getCONV_RATE(),
                r.getMJM_JUMLAH_USD(),
                r.getNO_POLIS(),
                r.getTAXNO() // pastikan ada di projection-mu
        ));

        em.flush();

        BigDecimal bal = ptcJmRepository.isBalanceJournal(noJm);
        if (bal != null && bal.compareTo(BigDecimal.ZERO) != 0) {
            throw new NotBalancedException("NoJM " + noJm + " not balance: " + bal);
        }
    }

    private MstPtcTm insertMstTm(MasterProduction masterProduction, Date dateNow, String noJm){
        MstPtcTm mstPtcTm = new MstPtcTm();
        mstPtcTm.setNoJm(noJm);
        mstPtcTm.setNoJmRef(masterProduction.getNoJm());
        mstPtcTm.setPosition(1);
        mstPtcTm.setTglJurnal(dateNow);
        mstPtcTm.setUserInput(512);
        ptcTmRepository.save(mstPtcTm);
        return mstPtcTm;
    }
}
