package id.co.msiglife.pollux.schduler.service.CheckLineBusiness;

import id.co.msiglife.pollux.schduler.dto.SuratAutoCancelDTO;
import id.co.msiglife.pollux.schduler.repository.MasterProductInsuredRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckLineBusinessService {

    @Autowired
    private MasterProductInsuredRepository masterProductInsuredRepository;

    public List<SuratAutoCancelDTO> checkLineBusiness(List<SuratAutoCancelDTO> listSuratAutoCancel){
        List<SuratAutoCancelDTO> result = new ArrayList<>();

        for (SuratAutoCancelDTO dto : listSuratAutoCancel) {
            String regSpaj = dto.getRegSpaj();
            String data = masterProductInsuredRepository.checkLiniBisnis(regSpaj);
            if (data != null) {
                //konven
                dto.setSyariah(false);
            } else {
                //syariah
                dto.setSyariah(true);
            }
            result.add(dto);
        }
        return result;
    }
}
