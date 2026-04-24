package id.co.msiglife.pollux.schduler.service.BcaRiplay;

import id.co.msiglife.pollux.schduler.dto.BcaRiplayDTO;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchingBcaRiplayService {

    @Autowired
    private BcaRiplayService batchService;

    public void processInBatch(List<BcaRiplayDTO> list) throws Exception {
        int batchSize = 100;
        /*
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            List<BcaRiplayDTO> batch = list.subList(i, end);
            System.out.println("Processing batch: " + i + " - " + end);
            System.out.println("Batch ke-" + ((i / batchSize) + 1));
            batchService.processBcaRiplayList(batch); // transactional per batch
        }
        */
        List<BcaRiplayDTO> batch = new ArrayList<>();
        BcaRiplayDTO dto = new BcaRiplayDTO();
        dto.setRegSpaj(ConvertUtil.safeString("09251446269"));
        dto.setPolicyNo(ConvertUtil.safeString("09517250000952"));
        dto.setInputDate(ConvertUtil.safeDate("2025-01-23 17:00:00.000"));
        dto.setGenerateRiplayDate(ConvertUtil.safeDate("2025-01-23 17:00:00.000"));
        dto.setNamaTertanggung(ConvertUtil.safeString("RICKY ARISAPUTRA GONADI"));
        dto.setDob(ConvertUtil.safeDate("1981-04-28"));
        dto.setLswfConfigAction(ConvertUtil.safeString("Inforced Policy"));
        dto.setEmailTertanggung(ConvertUtil.safeString("cobatest@gmail.com"));
        dto.setNamaCabang(ConvertUtil.safeString("PT BANK CENTRAL ASIA TBK CABANG DBKK"));
        dto.setPathFile(ConvertUtil.safeString(null));
        batch.add(dto);
        batchService.processBcaRiplayList(batch);
    }
}
