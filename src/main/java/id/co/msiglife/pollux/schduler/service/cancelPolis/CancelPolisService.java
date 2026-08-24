package id.co.msiglife.pollux.schduler.service.cancelPolis;

import id.co.msiglife.pollux.schduler.dto.CancelPolisDTO;
import id.co.msiglife.pollux.schduler.exception.WfConfigNotFoundException;
import id.co.msiglife.pollux.schduler.repository.MasterAutoCancelEmailLogRepository;
import id.co.msiglife.pollux.schduler.repository.MasterPolicyRepository;
import id.co.msiglife.pollux.schduler.util.ConvertUtil;
import id.co.msiglife.pollux.schduler.util.StaticValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CancelPolisService {

    @Autowired
    private MasterPolicyRepository masterPolicyRepository;

    @Autowired
    private MasterAutoCancelEmailLogRepository masterAutoCancelEmailLogRepository;

    @Autowired
    private SingleProcessCancelPolisService singleProcessCancelPolisService;

    public void processCancelPolisList(List<CancelPolisDTO> cancelPolisDTOList) {
        if (cancelPolisDTOList == null || cancelPolisDTOList.isEmpty()) return;

        for (CancelPolisDTO dto : cancelPolisDTOList) {
            try {
                System.out.println("In Progress canceled polis : " + dto.getRegSpaj());
                if (dto.isSyariah()) {
                    singleProcessCancelPolisService.processSingleCancelPolis(dto);
                } else {
                    singleProcessCancelPolisService.processSingleCancelPolis(dto);
                }
            } catch (WfConfigNotFoundException e) {
                masterAutoCancelEmailLogRepository.updatePolisFailed(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
                System.out.println("WF Config Not Found for RegSpaj: " + dto.getRegSpaj() + " -> " + e.getMessage());
            } catch (Exception e) {
                masterAutoCancelEmailLogRepository.updatePolisFailed(dto.getRegSpaj(), StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_DAYS);
                System.out.println("Unexpected error while processing RegSpaj: " + dto.getRegSpaj() + " -> " + e.getMessage());
            }
        }
    }

    public List<CancelPolisDTO> getCancelPolisList() {
        List<Integer> reminderTypes = Arrays.asList(StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_TYPE, StaticValue.SERTIFIKAT_DIBATALKAN_NOTIFICATION_WITHOUT_EMAIL_PDF_TYPE);

        List<Object[]> resultList = masterPolicyRepository.findPolicyTobeCancelled(reminderTypes);
        List<CancelPolisDTO> policyToBeCancelledList = new ArrayList<>();
        for (Object[] row : resultList) {
            CancelPolisDTO dto = new CancelPolisDTO();
            dto.setRegSpaj(ConvertUtil.safeString(row[0]));
            policyToBeCancelledList.add(dto);
        }
        return policyToBeCancelledList;
    }
}
